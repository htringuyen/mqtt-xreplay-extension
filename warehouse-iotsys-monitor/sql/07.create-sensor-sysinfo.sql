INSERT INTO sensorsys.SensorSystemInfo(warehouseId, temperatureSystemSensorTotal,
                                       humiditySystemSensorTotal, sideTemperatureSensorTotal,
                                       sideHumiditySensorTotal, statusContainerSensorEach)
SELECT wh.id, 1, 1, 4, 4, 1
FROM business.Warehouse as wh