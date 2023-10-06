export interface EpsonUSBPrinterInfo {
  productId?: number;
  vendorId: number;
  productName: string;
  connected: boolean;
}

export interface EpsonUSBPrinterLineEntry {
  lineText?: string;
  lineStyleList?: string[];
  lineCommandList?: string[];
}

export interface EpsonUSBPrinterPlugin {
  getPrinterList(): Promise<{ printerList: EpsonUSBPrinterInfo[]; }>;

  hasPermission(options: { vendorId: number, productId: number }): Promise<{ permission: boolean; }>;

  connectToPrinter(options: { vendorId: number, productId: number }): Promise<{ connected: boolean; }>;

  print(options: { printObject: string }): Promise<void>;

  printHexArray(options: { content: string }): Promise<void>;

}