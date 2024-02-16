export interface EpsonUSBPrinterInfo {
  productId?: number;
  vendorId: number;
  productName: string;
  connected: boolean;
  deviceId: number;
  // serial: string;
}

export interface EpsonUSBPrinterLineEntry {
  lineText?: string;
  lineStyleList?: string[];
  lineCommandList?: string[];
}

export interface EpsonUSBPrinterPlugin {
  getPrinterList(): Promise<{ printerList: EpsonUSBPrinterInfo[]; }>;

  hasPermission(options: { deviceId: number}): Promise<{ permission: boolean; }>;

  retrieveSerial(options: { deviceId: number}): Promise<{ serial: string; }>;

  connectToPrinter(options: { deviceId: number, vendorId: number, productId: number}): Promise<{ connected: boolean; }>;

  print(options: { printObject: string }): Promise<void>;

  printHexArray(options: { content: string }): Promise<void>;

}