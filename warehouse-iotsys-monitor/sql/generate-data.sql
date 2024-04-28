
DECLARE @countingCityCode varchar(20),
    @whCount int;

DECLARE WhCountingCursor CURSOR FOR
    SELECT cityCode as countingCityCode, SUM(typeWarehouseEach) as whCount
    FROM tmp.LocationMeta cross join business.WarehouseType
    GROUP BY cityCode;

-- OPEN WhMetaCursor;
--
-- FETCH NEXT FROM WhMetaCursor
-- INTO @cityCode, @cityName, @typeWarehouseEach, @containerEach, @typeId, @typeCode;

OPEN WhCountingCursor;
FETCH NEXT FROM WhCountingCursor
INTO @countingCityCode, @whCount;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @cityCode varchar(20),
        @cityName varchar(20),
        @typeWarehouseEach int,
        @containerEach int,
        @typeId int,
        @typeCode varchar(20);

    DECLARE WhMetaCursor CURSOR FOR
        SELECT cityCode, cityName, typeWarehouseEach, containerEach, id as typeId, code as typeCode
        FROM tmp.LocationMeta cross join business.WarehouseType
        WHERE cityCode = @countingCityCode;

    OPEN WhMetaCursor;
    FETCH NEXT FROM WhMetaCursor
        INTO @cityCode, @cityName, @typeWarehouseEach, @containerEach, @typeId, @typeCode;

    DECLARE @wi int;
    SET @wi = 1;

    WHILE @wi <= @whCount
    BEGIN

        SET wareti
        INSERT INTO business.Warehouse(typeId, name, address)
        VALUES (@typeId, 'WH-' + @cityCode + '-' + @typeCode, )
    END
END



;
with WhMeta as (
    select *
    from tmp.LocationMeta cross join business.WarehouseType
)
select * from WhMeta;

select cityCode as whCityCode, sum(typeWarehouseEach) as whCount
from tmp.LocationMeta cross join business.WarehouseType
group by cityCode






























