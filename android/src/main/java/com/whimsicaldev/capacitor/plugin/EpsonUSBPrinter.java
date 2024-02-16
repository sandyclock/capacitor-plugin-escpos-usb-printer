package com.whimsicaldev.capacitor.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.Logger;
import com.getcapacitor.PluginCall;

public class EpsonUSBPrinter {
    private final Context context;
    private final String actionString;
    private final UsbManager manager;
    private final List<UsbDevice> deviceList;
  private UsbInterface usbInterfaceConnected;
  private UsbEndpoint usbEndpointConnected;
    private final ObjectMapper objectMapper = new ObjectMapper();
  public UsbDeviceConnection connection;

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public EpsonUSBPrinter(Context context) {
        this.context = context;
        this.actionString = this.context.getPackageName() + ".USB_PERMISSION";
        this.manager =  (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
        this.deviceList = new ArrayList<>();
    }

  private void requestPermission(UsbDevice _usbDevice, BroadcastReceiver _receiver) {
    PendingIntent permissionIntent = PendingIntent.getBroadcast(
      this.context,
      0,
      new Intent(actionString),
      android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
    );
    IntentFilter filter = new IntentFilter(actionString);
//    Logger.info("************** receiver is *************");
//    Logger.info(String.valueOf(_receiver));
//    Logger.info(this.actionString);
//    Logger.info(String.valueOf(_usbDevice));
//    Logger.info(String.valueOf(permissionIntent));
    this.context.registerReceiver(_receiver, filter);
//    Logger.info("*************** request permission check 2 ************");
    this.manager.requestPermission(_usbDevice, permissionIntent);
  }

    public List<Map> getPrinterList() {
        List<Map> printerList = new ArrayList<>();

        HashMap<String, UsbDevice> deviceList = this.manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();

      if (isAPrinter(usbDevice)) {
                Map printerInfo = new HashMap();
        printerInfo.put(EpsonUSBPrinterConstant.Info.productId, usbDevice.getProductId());
        printerInfo.put(EpsonUSBPrinterConstant.Info.productName, usbDevice.getProductName());
        printerInfo.put(EpsonUSBPrinterConstant.Info.connected, false);
        printerInfo.put(EpsonUSBPrinterConstant.Info.deviceName, usbDevice.getDeviceName());
        printerInfo.put(EpsonUSBPrinterConstant.Info.manufacturer, usbDevice.getManufacturerName());
        printerInfo.put(EpsonUSBPrinterConstant.Info.deviceId, usbDevice.getDeviceId());
        printerInfo.put(EpsonUSBPrinterConstant.Info.vendorId, usbDevice.getVendorId());
//        printerInfo.put(EpsonUSBPrinterConstant.Info.serial, usbDevice.getSerialNumber());


//        printerInfo.put("productId", usbDevice.getProductId());
//                printerInfo.put("productName", usbDevice.getProductName());
//                printerInfo.put("connected", false);
//        printerInfo.put("deviceName", usbDevice.getDeviceName());
//        printerInfo.put("manufacturer", usbDevice.getManufacturerName());
//        printerInfo.put("deviceId", usbDevice.getDeviceId());
//        printerInfo.put("vendorId", usbDevice.getVendorId());
//        printerInfo.put("version", usbDevice.getVersion());

                printerList.add(printerInfo);
                this.deviceList.add(usbDevice);
            }
        }

        return printerList;
    }

    private boolean isAPrinter(UsbDevice usbDevice) {
    for (int i = 0; i < usbDevice.getInterfaceCount(); i += 1) {
      UsbInterface _usbInterface = usbDevice.getInterface(i);
      Logger.info("usb interface counts:");
      Logger.info(String.valueOf(_usbInterface.getEndpointCount()));

      for (int j = 0; j < _usbInterface.getEndpointCount(); j++) {
        UsbEndpoint _usbEndpoint = _usbInterface.getEndpoint(j);

        if (UsbConstants.USB_ENDPOINT_XFER_BULK == _usbEndpoint.getType() && _usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
          return true;
        }
            }
        }
        return false;
    }

  public void hasPermission(PluginCall call, Integer deviceId) {
        UsbDevice selectedDevice = null;
    HashMap<String, UsbDevice> deviceList = this.manager.getDeviceList();
    for (UsbDevice device : deviceList.values()) {
      if (Objects.equals(deviceId, device.getDeviceId())){// && Objects.equals(vendorId, device.getVendorId())) {
                selectedDevice = device;
                setUsbInterfaceAndEndpoint(selectedDevice);
                break;
            }
        }

    if (selectedDevice == null) {
      call.reject("Device with device id " + deviceId + " cannot be found.");
      return;
    }
    boolean result = this.manager.hasPermission(selectedDevice);
    JSObject retVal = new JSObject();
    retVal.put("permission", result);
    call.resolve(retVal);
  }

  public void retrieveSerial(PluginCall call, Integer deviceId) {
    UsbDevice selectedDevice = null;
    HashMap<String, UsbDevice> deviceList = this.manager.getDeviceList();
    for (UsbDevice device : deviceList.values()) {
      if (Objects.equals(deviceId, device.getDeviceId())){// && Objects.equals(vendorId, device.getVendorId())) {
        selectedDevice = device;
        setUsbInterfaceAndEndpoint(selectedDevice);
        break;
      }
    }

    if (selectedDevice == null) {
      call.reject("Device with device id " + deviceId + " is not found.");
      return;
    }

    String actionString = this.actionString;
    UsbDevice _device = selectedDevice;
    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (actionString.equals(action)) {
          synchronized (this) {
            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
              try {
                  String _serial = usbDevice.getSerialNumber();
                  JSObject jsObject = new JSObject();
                  jsObject.put("serial", _serial);
                call.resolve(jsObject);
//                EpsonUSBPrinter.this.connection = manager.openDevice(_device);
//                JSObject jsObject = new JSObject();
//                jsObject.put("connected", true);
//
//                call.resolve(jsObject);
              } catch (Exception e) {
                EpsonUSBPrinter.this.connection = null;
                call.reject("Failed to retrieve USB device information (device id: " + deviceId + ") due to " + e.getMessage());
              }
              return;
            }
            call.reject("Fail to retrieve device information because the access permission is not granted.");

          }
        }
      }

      ;

    };
    this.requestPermission(_device, usbReceiver);
  }

  public void _connectToPrinter(PluginCall call, Integer deviceId) {
    UsbDevice selectedDevice = null;
    HashMap<String, UsbDevice> deviceList = this.manager.getDeviceList();
    for (UsbDevice device : deviceList.values()) {
      if (Objects.equals(deviceId, device.getDeviceId())){// && Objects.equals(vendorId, device.getVendorId())) {
        selectedDevice = device;
        setUsbInterfaceAndEndpoint(selectedDevice);
        break;
      }
    }

    if (selectedDevice == null) {
      call.reject("Device with device id " + deviceId + " is not found.");
      return;
        }

    String actionString = this.actionString;
    UsbDevice _device = selectedDevice;
    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (actionString.equals(action)) {
          synchronized (this) {
            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
        try {
                EpsonUSBPrinter.this.connection = manager.openDevice(_device);
                JSObject jsObject = new JSObject();
                jsObject.put("connected", true);

                call.resolve(jsObject);
              } catch (Exception e) {
                EpsonUSBPrinter.this.connection = null;
                call.reject("Failed to establish connection to device (device id: " + deviceId + ") due to " + e.getMessage());
              }
            }
          }
        }
    }

      ;

    };
    this.requestPermission(_device, usbReceiver);
  }

    private void setUsbInterfaceAndEndpoint(UsbDevice usbDevice) {
    for (int i = 0; i < usbDevice.getInterfaceCount(); i += 1) {
      UsbInterface _usbInterface = usbDevice.getInterface(i);
      for (int j = 0; j < _usbInterface.getEndpointCount(); j++) {
        UsbEndpoint _usbEndpoint = _usbInterface.getEndpoint(j);
        if (UsbConstants.USB_ENDPOINT_XFER_BULK == _usbEndpoint.getType() && UsbConstants.USB_DIR_OUT == _usbEndpoint.getDirection()) {
          this.usbInterfaceConnected = _usbInterface;
          this.usbEndpointConnected = _usbEndpoint;
                    return;
                }
            }
        }
    }

    public void print(String printObject) throws Exception {
    if (this.connection == null) {
            throw new Exception("Currently not connected to a device.");
    } else if (this.usbInterfaceConnected == null) {
            throw new Exception("Usb interface is not properly set.");
        }
    List<EpsonUSBPrinterLineEntry> printObjectList = this.objectMapper.readValue(printObject, new TypeReference<>() {
    });

    this.connection.claimInterface(this.usbInterfaceConnected, true);
        byte[] LN = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(EpsonUSBPrinterConstant.LN);
        byte[] RESET = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(EpsonUSBPrinterConstant.RESET);

    for (EpsonUSBPrinterLineEntry lineEntry : printObjectList) {
      connection.bulkTransfer(usbEndpointConnected, RESET, RESET.length, 10000);
      if (lineEntry.getLineStyleList() != null) {
        for (String style : lineEntry.getLineStyleList()) {
                    byte[] styleValue = EpsonUSBPrinterConstant.EPSON_STYLE_LIST.get(style);
          if (styleValue != null) {
            connection.bulkTransfer(usbEndpointConnected, styleValue, styleValue.length, 10000);
                    }
                }
            }

      if (lineEntry.getLineText() != null) {
                String printData = lineEntry.getLineText();
        this.connection.bulkTransfer(this.usbEndpointConnected, printData.getBytes(), printData.getBytes().length, 10000);
        this.connection.bulkTransfer(this.usbEndpointConnected, LN, LN.length, 10000);
            }

      if (lineEntry.getLineCommandList() != null) {
        for (String command : lineEntry.getLineCommandList()) {
                    byte[] commandValue = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(command);
          if (commandValue != null) {
            connection.bulkTransfer(usbEndpointConnected, commandValue, commandValue.length, 10000);
                    }
                }
            }
        }

        // line feed to push the prints beyond the printer cover
    for (int i = 0; i < 6; i += 1) {
      this.connection.bulkTransfer(this.usbEndpointConnected, LN, LN.length, 10000);
        }

    this.connection.releaseInterface(this.usbInterfaceConnected);
    this.connection.close();
    this.connection = null;
    }

  public void printRaw(byte[] bytes) throws Exception {
    if (this.connection == null) {
      throw new Exception("Currently not connected to a device.");
    } else if (this.usbInterfaceConnected == null) {
      throw new Exception("Usb interface is not properly set.");
    }
//    List<EpsonUSBPrinterLineEntry> printObjectList = this.objectMapper.readValue(printObject, new TypeReference<>() {});

    this.connection.claimInterface(this.usbInterfaceConnected, true);
    this.connection.bulkTransfer(this.usbEndpointConnected, bytes, bytes.length, 10000);
//    byte[] LN = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(EpsonUSBPrinterConstant.LN);
//    byte[] RESET = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(EpsonUSBPrinterConstant.RESET);
//
//    for(EpsonUSBPrinterLineEntry lineEntry: printObjectList) {
//      connection.bulkTransfer(usbEndpoint, RESET, RESET.length, 10000);
//      if(lineEntry.getLineStyleList() != null) {
//        for(String style: lineEntry.getLineStyleList()) {
//          byte[] styleValue = EpsonUSBPrinterConstant.EPSON_STYLE_LIST.get(style);
//          if(styleValue != null) {
//            connection.bulkTransfer(usbEndpoint, styleValue, styleValue.length, 10000);
//          }
//        }
//      }
//
//      if(lineEntry.getLineText() != null) {
//        String printData = lineEntry.getLineText();
//        this.connection.bulkTransfer(this.usbEndpoint, printData.getBytes(), printData.getBytes().length, 10000);
//        this.connection.bulkTransfer(this.usbEndpoint, LN, LN.length, 10000);
//      }
//
//      if(lineEntry.getLineCommandList() != null) {
//        for(String command: lineEntry.getLineCommandList()) {
//          byte[] commandValue = EpsonUSBPrinterConstant.EPSON_COMMAND_LIST.get(command);
//          if(commandValue != null) {
//            connection.bulkTransfer(usbEndpoint, commandValue, commandValue.length, 10000);
//          }
//        }
//      }
//    }
//
//    // line feed to push the prints beyond the printer cover
//    for(int i = 0; i < 6; i+=1) {
//      this.connection.bulkTransfer(this.usbEndpoint, LN, LN.length, 10000);
//    }


    this.connection.releaseInterface(this.usbInterfaceConnected);
    this.connection.close();
    this.connection = null;
  }
}