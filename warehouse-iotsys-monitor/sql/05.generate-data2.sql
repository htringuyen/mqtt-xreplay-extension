DECLARE @cityCode varchar(20),
    @cityName varchar(20),
    @typeWarehouseEach int,
    @containerEach int,
    @typeId int,
    @typeCode varchar(20);

DECLARE WhMetaCursor CURSOR FOR
    SELECT cityCode, cityName, typeWarehouseEach, containerEach, id as typeId, code as typeCode
    FROM tmp.LocationMeta cross join business.WarehouseType
    ORDER BY cityCode;

OPEN WhMetaCursor;
FETCH NEXT FROM WhMetaCursor
    INTO @cityCode, @cityName, @typeWarehouseEach, @containerEach, @typeId, @typeCode;

DECLARE @currentCityCode varchar(25);
SET @currentCityCode = 'UNKNOWN-SHOULD-BE-CHANGED';

DECLARE @cityBasedWhOrdinal int;

WHILE @@FETCH_STATUS = 0
BEGIN
    IF (@currentCityCode <> @cityCode)
    BEGIN
        SET @currentCityCode = @cityCode;
        SET @cityBasedWhOrdinal = 1
    END

    DECLARE @i int;
    SET @i = 1;
    WHILE @i <= @typeWarehouseEach
    BEGIN
        DECLARE @warehouseCode varchar(30);
        SET @warehouseCode = @cityCode + '-' + FORMAT(@cityBasedWhOrdinal, '000');
        INSERT INTO business.Warehouse(code, typeId, address)
        SELECT @warehouseCode, @typeId, @cityName;

        -- create containers
        DECLARE @j int;
        SET @j = 1;
        WHILE @j <= @containerEach
        BEGIN
            INSERT INTO business.Container(warehouseId, name, ordinal, ftVolume)
            SELECT wh.id, 'Standard Container', @j, 60
            FROM business.Warehouse as wh
            WHERE code = @warehouseCode;
            SET @j = @j + 1;
        END

        -- update counters
        SET @cityBasedWhOrdinal = @cityBasedWhOrdinal + 1;
        SET @i = @i + 1;
    END

    FETCH NEXT FROM WhMetaCursor
        INTO @cityCode, @cityName, @typeWarehouseEach, @containerEach, @typeId, @typeCode;
END;

CLOSE WhMetaCursor;
DEALLOCATE WhMetaCursor;