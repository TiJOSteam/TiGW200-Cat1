import java.io.IOException;

import tigateway.TiGW200;
import tigateway.modbus.rtu.ModbusRTU;
import tigateway.peripheral.TiRS485;
import tijos.framework.devicecenter.TiUART;
import tijos.framework.util.Delay;

/**
 * MOBDBUS RTU 例程
 * @author TiJOS
 *
 */
public class modbusSample {
	public static void main(String[] args) {

		try {
			System.out.println("This is a modbus rtu sample.");

			// 获取TiGW200对象并启动看门狗
			TiGW200 gw200 = TiGW200.getInstance();


			// 获取第1路RS485 9600 8 1 N
			TiRS485 rs485 = gw200.getRS485(0, 9600, TiUART.PARITY_NONE);

			// MODBUS RTU
			// 通讯超时500 ms
			ModbusRTU modbusRtu = new ModbusRTU(rs485, 500);

			// MODBUS 数据处理
			// 每5秒进行一次数据处理同时绿灯亮一次
			while (true) {
				gw200.greenLED().turnOn();
				MonitorProcess(modbusRtu);
				gw200.blueLED().turnOff();
				Delay.msDelay(5000);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 通过RS485基于MODBUS协议读取设备数据
	 *
	 * @param modbusRtu
	 */
	public static void MonitorProcess(ModbusRTU modbusRtu) {
		try {
			// MODBUS device id 设备地址
			int deviceId = 1;
			// Input Register 开始地址
			int startAddr = 0;

			// Read 2 registers from start address 读取个数
			int count = 2;

			// 读取Holding Register
			modbusRtu.initReadHoldingsRequest(deviceId, startAddr, count);

			// 下发并获取响应
			int result = modbusRtu.execRequest();

			// 成功
			if (result == ModbusRTU.RESULT_OK) {

				int humdity = modbusRtu.getResponseInt16(startAddr, false);
				int temperature = modbusRtu.getResponseInt16(startAddr + 1, false);

				System.out.println("temp = " + temperature + " humdity = " + humdity);
			} else {
				System.out.println("Modbus error " + modbusRtu.getResultAsString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
