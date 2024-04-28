DECLARE @warehouseCode varchar(100),
    @totalContainer int,
    @temperatureSystemSensorTotal int,
    @humiditySystemSensorTotal int,
    @sideTemperatureSensorTotal int,
    @sideHumiditySensorTotal int,
    @containerStatusSensorEach int;

DECLARE SensorSysInfo CURSOR FOR
    SELECT wh.code, cnt.totalContainer, ssi.temperatureSystemSensorTotal,
           ssi.humiditySystemSensorTotal, ssi.sideTemperatureSensorTotal,
           ssi.sideHumiditySensorTotal, ssi.statusContainerSensorEach as containerStatusSensorEach
    FROM sensorsys.SensorSystemInfo ssi
    LEFT JOIN business.Warehouse wh
    ON ssi.warehouseId = wh.id
    LEFT JOIN (
        SELECT warehouseId, count(*) as totalContainer
        FROM business.Container
        GROUP BY warehouseId
    ) cnt
    ON wh.id = cnt.warehouseId;


OPEN SensorSysInfo;
FETCH NEXT FROM SensorSysInfo INTO @warehouseCode, @totalContainer, @temperatureSystemSensorTotal,
    @humiditySystemSensorTotal, @sideTemperatureSensorTotal, @sideHumiditySensorTotal, @containerStatusSensorEach;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- create temperature system sensors
    DECLARE @i int = 1;
    WHILE @i <= @temperatureSystemSensorTotal
    BEGIN
        INSERT INTO sensorcomm.SensorParam (sensorId, warehouseCode, containerOrdinal, sensorOrdinal)
        VALUES (@warehouseCode + '|tempsys|' + FORMAT(@i, '000'), @warehouseCode, 'NOA', FORMAT(@i, '000'))
        SET @i = @i + 1;
    END;

    -- create humidity system sensors
    SET @i = 1;
    WHILE @i <= @humiditySystemSensorTotal
    BEGIN
        INSERT INTO sensorcomm.SensorParam (sensorId, warehouseCode, containerOrdinal, sensorOrdinal)
        VALUES (@warehouseCode + '|humisys|' + FORMAT(@i, '000'), @warehouseCode, 'NOA', FORMAT(@i, '000'))
        SET @i = @i + 1;
    END;

    -- create side temperature sensors
    SET @i = 1;
    WHILE @i <= @sideTemperatureSensorTotal
    BEGIN
        INSERT INTO sensorcomm.SensorParam (sensorId, warehouseCode, containerOrdinal, sensorOrdinal)
        VALUES (@warehouseCode + '|sidetemp|' + FORMAT(@i, '000'), @warehouseCode, 'NOA', FORMAT(@i, '000'))
        SET @i = @i + 1;
    END;

    -- create side humidity sensors
    SET @i = 1;
    WHILE @i <= @sideHumiditySensorTotal
    BEGIN
        INSERT INTO sensorcomm.SensorParam (sensorId, warehouseCode, containerOrdinal, sensorOrdinal)
        VALUES (@warehouseCode + '|sidehumi|' + FORMAT(@i, '000'), @warehouseCode, 'NOA', FORMAT(@i, '000'))
        SET @i = @i + 1;
    END;

    -- create container status sensors
    SET @i = 1;
    WHILE @i <= @totalContainer
    BEGIN
        DECLARE @j int = 1;
        WHILE @j <= @containerStatusSensorEach
        BEGIN
            INSERT INTO sensorcomm.SensorParam (sensorId, warehouseCode, containerOrdinal, sensorOrdinal)
            VALUES (@warehouseCode + '|cont-' + FORMAT(@i, '000') + '|status|' + FORMAT(@j, '000'), @warehouseCode, FORMAT(@i, '000'), FORMAT(@j, '000'))
            SET @j = @j + 1;
        END;
        SET @i = @i + 1;
    END;

    FETCH NEXT FROM SensorSysInfo INTO @warehouseCode, @totalContainer, @temperatureSystemSensorTotal,
        @humiditySystemSensorTotal, @sideTemperatureSensorTotal, @sideHumiditySensorTotal, @containerStatusSensorEach;
END;

CLOSE SensorSysInfo;
DEALLOCATE SensorSysInfo;









