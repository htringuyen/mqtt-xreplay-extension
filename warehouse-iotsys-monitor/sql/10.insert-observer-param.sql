INSERT INTO sensorcomm.Observer (name, password, roleName)
VALUES
    ('toplevelmanager01', 'obs1234', 'TOP_LEVEL_MANAGER'),
    ('toplevelobserver01', 'obs1234', 'TOP_LEVEL_OBSERVER'),
    ('warehousescopeobserver01', 'obs1234', 'WAREHOUSE_SCOPE_OBSERVER'),
    ('containerscopeobserver01', 'obs1234', 'CONTAINER_SCOPE_OBSERVER');

INSERT INTO sensorcomm.ObserverParam (observerName, warehouseCode, containerOrdinal, sensorOrdinal)
VALUES
    ('toplevelmanager01', 'ALL', 'ALL', 'ALL'),
    ('toplevelobserver01', 'ALL', 'ALL', 'ALL'),
    ('warehousescopeobserver01', 'VN-BD-001', 'ALL', 'ALL'),
    ('containerscopeobserver01', 'VN-BD-002', '001', 'ALL');
