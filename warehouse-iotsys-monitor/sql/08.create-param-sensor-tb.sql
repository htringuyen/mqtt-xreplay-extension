create table sensorcomm.SensorParam (
    sensorId varchar(255) primary key,
    warehouseCode varchar(100) not null,
    containerOrdinal varchar(100) not null,
    sensorOrdinal varchar(20) not null
)
