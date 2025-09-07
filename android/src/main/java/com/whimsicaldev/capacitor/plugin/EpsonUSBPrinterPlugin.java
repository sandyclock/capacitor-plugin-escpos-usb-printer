package com.whimsicaldev.capacitor.plugin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;

import android.util.Base64;

@CapacitorPlugin(name = "EpsonUSBPrinter")
public class EpsonUSBPrinterPlugin extends Plugin {

    private EpsonUSBPrinter implementation;

    @Override
    public void load() {
        implementation = new EpsonUSBPrinter(getContext());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void getPrinterList(PluginCall call) {
        try {
            JSObject jsObject = new JSObject();
            JSONArray jsonArray = new JSONArray(implementation.getPrinterList());
            jsObject.put("printerList", jsonArray);
            call.resolve(jsObject);
        } catch(Exception e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
  public void hasPermission(PluginCall call) {
    if (!call.hasOption(EpsonUSBPrinterConstant.Info.deviceId)) {
//      if (!call.hasOption("productId")) {
      call.reject("Device id is not provided.");
      return;
    }
    else if (call.hasOption(EpsonUSBPrinterConstant.Info.deviceId) && call.getInt(EpsonUSBPrinterConstant.Info.deviceId) == null) {
//    } else if (call.hasOption("productId") && call.getInt("productId") == null) {

      call.reject("Product id is of incorrect type, please provide an integer.");
      return;
    }

//      if (!call.hasOption(EpsonUSBPrinterConstant.Info.vendorId)) {
//        call.reject("Vendor id is not provided.");
//        return;
//      } else if (call.hasOption(EpsonUSBPrinterConstant.Info.vendorId) && call.getInt(EpsonUSBPrinterConstant.Info.vendorId) == null) {
//        call.reject("Vendor id is of incorrect type, please provide an integer.");
//        return;
//      }

      Integer deviceId = call.getInt(EpsonUSBPrinterConstant.Info.deviceId);

//    Integer vendorId = call.getInt(EpsonUSBPrinterConstant.Info.vendorId);

      this.implementation.hasPermission(call, deviceId);

  }

  @PluginMethod
  public void retrieveSerial(PluginCall call) {
    if (!call.hasOption(EpsonUSBPrinterConstant.Info.deviceId)) {
//      if (!call.hasOption("productId")) {
      call.reject("Device id is not provided.");
      return;
    }
    else if (call.hasOption(EpsonUSBPrinterConstant.Info.deviceId) && call.getInt(EpsonUSBPrinterConstant.Info.deviceId) == null) {
//    } else if (call.hasOption("productId") && call.getInt("productId") == null) {

      call.reject("Device id is of incorrect type, please provide an integer.");
      return;
    }

//      if (!call.hasOption(EpsonUSBPrinterConstant.Info.vendorId)) {
//        call.reject("Vendor id is not provided.");
//        return;
//      } else if (call.hasOption(EpsonUSBPrinterConstant.Info.vendorId) && call.getInt(EpsonUSBPrinterConstant.Info.vendorId) == null) {
//        call.reject("Vendor id is of incorrect type, please provide an integer.");
//        return;
//      }

    Integer deviceId = call.getInt(EpsonUSBPrinterConstant.Info.deviceId);

//    Integer vendorId = call.getInt(EpsonUSBPrinterConstant.Info.vendorId);

    this.implementation.retrieveSerial(call, deviceId);

  }

  @PluginMethod
    public void connectToPrinter(PluginCall call) {
    if (!call.hasOption(EpsonUSBPrinterConstant.Info.deviceId)) {
            call.reject("Product id is not provided.");
      return;
    } else if (call.hasOption(EpsonUSBPrinterConstant.Info.deviceId) && call.getInt(EpsonUSBPrinterConstant.Info.deviceId) == null) {
            call.reject("Product id is of incorrect type, please provide an integer.");
      return;
        }

//    if (!call.hasOption(EpsonUSBPrinterConstant.Info.vendorId)) {
//      call.reject("Vendor id is not provided.");
//      return;
//    } else if (call.hasOption(EpsonUSBPrinterConstant.Info.vendorId) && call.getInt(EpsonUSBPrinterConstant.Info.vendorId) == null) {
//      call.reject("Vendor id is of incorrect type, please provide an integer.");
//      return;
//    }

//    if (!call.hasOption("productId")) {
//      call.reject("Product id is not provided.");
//      return;
//    } else if (call.hasOption("productId") && call.getInt("productId") == null) {
//      call.reject("Product id is of incorrect type, please provide an integer.");
//      return;
//    }

    Integer deviceId = call.getInt(EpsonUSBPrinterConstant.Info.deviceId);

//    Integer vendorId = call.getInt(EpsonUSBPrinterConstant.Info.vendorId);

    this.implementation._connectToPrinter(call, deviceId);

    }

    @PluginMethod
    public void print(PluginCall call) {
    if (!call.hasOption("printObject")) {
            call.reject("Print Object is not provided.");
    } else if (call.hasOption("printObject") && call.getString("printObject") == null) {
            call.reject("Print object is of incorrect type, please provide a string.");
        }

        String printObject = call.getString("printObject");
        int lineFeed = call.getInt("lineFeed", 0);
        try {
            implementation.print(printObject, lineFeed);
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

  @PluginMethod
  public void printHexArray(PluginCall call) {
    if (!call.hasOption("content")) {
      call.reject("Print Object is not provided.");
    } else if (call.hasOption("content") && call.getString("content") == null) {
      call.reject("Print object is of incorrect type, please provide a string.");
    }

    String hexArray = call.getString("content");

    try {
      byte[] bytes = Base64.decode(hexArray, Base64.DEFAULT);
      implementation.printRaw(bytes);
      call.resolve();
    } catch (Exception e) {
      call.reject(e.getMessage());
    }
  }

}