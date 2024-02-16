# capacitor-plugin-epson-usb-printer

Capacitor Plugin for printing to epson usb printer

## Install

```bash
npm install capacitor-plugin-epson-usb-printer
npx cap sync
```

## API

<docgen-index>

* [`getPrinterList()`](#getprinterlist)
* [`hasPermission(...)`](#haspermission)
* [`connectToPrinter(...)`](#connecttoprinter)
* [`print(...)`](#print)
* [`printHexArray(...)`](#printhexarray)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getPrinterList()

```typescript
getPrinterList() => Promise<{ printerList: EpsonUSBPrinterInfo[]; }>
```

**Returns:** <code>Promise&lt;{ printerList: EpsonUSBPrinterInfo[]; }&gt;</code>

--------------------


### hasPermission(...)

```typescript
hasPermission(options: { deviceId: number; }) => Promise<{ permission: boolean; }>
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ deviceId: number; }</code> |

**Returns:** <code>Promise&lt;{ permission: boolean; }&gt;</code>

--------------------


### connectToPrinter(...)

```typescript
connectToPrinter(options: { deviceId: number; vendorId: number; productId: number; }) => Promise<{ connected: boolean; }>
```

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code>{ deviceId: number; vendorId: number; productId: number; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; }&gt;</code>

--------------------


### print(...)

```typescript
print(options: { printObject: string; }) => Promise<void>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ printObject: string; }</code> |

--------------------


### printHexArray(...)

```typescript
printHexArray(options: { content: string; }) => Promise<void>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ content: string; }</code> |

--------------------


### Interfaces


#### EpsonUSBPrinterInfo

| Prop              | Type                 |
| ----------------- | -------------------- |
| **`productId`**   | <code>number</code>  |
| **`vendorId`**    | <code>number</code>  |
| **`productName`** | <code>string</code>  |
| **`connected`**   | <code>boolean</code> |
| **`deviceId`**    | <code>number</code>  |
| **`serial`**      | <code>string</code>  |

</docgen-api>
