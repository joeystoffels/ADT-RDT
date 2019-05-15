```sql
use master
go

create database han
go

use han
go

create schema dbo
go

create table crypto
(
	timestamp datetime not null,
	value float not null
		constraint crypto_pk
			primary key nonclustered
)
go
```


