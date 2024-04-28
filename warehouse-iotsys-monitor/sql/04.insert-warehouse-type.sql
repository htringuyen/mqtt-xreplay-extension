INSERT INTO business.WarehouseType (code, temperatureLower, temperatureUpper, humidityLower, humidityUpper)
VALUES
    ('FROZEN-TX', -24, -18, 0, 2),
    ('FROZEN-TL', -18, -12, 2, 4),
    ('FROZEN-TM', -12, -5, 2, 4),
    ('FROZEN-TS', -5, 0, 2, 5),
    ('COLD-TX', 0, 5, 4, 6),
    ('COLD-TL', 5, 10, 5, 8),
    ('COLD-TM', 10, 15, 5, 10),
    ('COOL-TX', 15, 18, 6, 12),
    ('COOL-TL', 18, 22, 8, 15),
    ('COOL-TS', 22, 27, 10, 20),
    ('AMBIENT', 25, 38, 20, 50);
