# Flyway Migration
This directory contains all Flyway migration scripts to be executed when starting to run the microservice.

## General Tips
1. Name the Flyway script in this naming convention, `V{version.number}__{meaningful.name}.sql`. E.g. `V3__create_customer_profiles_table.sql`.
2. Always use major version only, unless special cases. Usually minor versions are introduced for [hot fixes](https://flywaydb.org/documentation/faq#hot-fixes).
3. When creating a new table, do not specify the database engine (e.g. InnoDB) nor the encoding (e.g. UTF-8). Let it uses the default set in the database instance.
4. When commenting out SQL lines, use `-- ` with a whitespace instead of `--`. E.g. do `-- This is a comment` instead of `--This is a comment`. See [Flyway GitHub #2081](https://github.com/flyway/flyway/issues/2081).
5. Recommend to separate different SQL command types into multiple SQL files. For example, for a task to create a table then insert master data, do the `CREATE TABLE` and `INSERT INTO` in two SQL files, e.g. `V1__create_sample_table.sql` and `V2__insert_sample.table.sql`. 

## Flyway Version Conflict
In parallel development, Flyway version conflict could happen.

Below is a sample case:
* In `FEBRUARY` release, the latest Flyway script is `V35__insert_master_data.sql`.
* The development of `MARCH` and `APRIL` releases happen in parallel.
* In `APRIL` branch, Foo introduced `V36__create_config_table.sql`.
* After that, in `MARCH` branch, Bar introduced `V36__truncate_master_data.sql` and `V37__insert_master_data.sql`.
* When merging `MARCH` to `APRIL`, conflicts happens at V36.
* To resolve, we need to shift the `APRIL` scripts to version higher than V37:
	1. Manually undo the changes of `V36__create_config_table.sql`.
	2. Remove the row of `V36__create_config_table.sql` in `flyway_schema_history` table.
	3. Rename `V36__create_config_table.sql` to `V38__create_config_table.sql`.
	4. Re-run Flyway migration.