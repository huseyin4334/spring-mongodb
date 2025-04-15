# Numbers On MongoDB
- Integers
  - int32
  - (-2147483648 to 2,147483647)
- Longs
  - int64
  - (-9,223,372,036,854,775,808 to 9,223,372,036,854,775,807)
- Doubles
  - 64bit
  - (-1.7976931348623157E308 to 1.7976931348623157E308)
  - Also decimal digits (15 digits)
- Decimal (high precision double)
  - 128bit
  - (-1.0 x 10^-6143 to 1.0 x 10^6144)
  - Also decimal digits (34 digits)

```bash
db.numbers.insertMany(
  [
    { type: "int32", b: NumberInt("2147483647") },
    { type: "int64", b: NumberLong("9223372036854775807") },
    { type: "double", b: NumberDouble("1.7976931348623157E308") },
    { type: "decimal", b: NumberDecimal("1.7976931348623157E308") }
  ]
)
```