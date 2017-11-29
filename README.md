# Fast Fake Products Generator for Akeneo PIM 2.

## Overview
This tool generates products tables data file that can be re-imported wih very fast MySQL native tools.

## How to use it?

### Pre-requisites
You need a PIM installation with all structural entities already configured (attributes, families, categories, attribute options, etc...), but without product.

### Generating data files
```bash
usage: <akeneo-product-generator-full-jar-file>
 -u <DATABASE-URL>     Database JDBC URL. Required.
 -c <PRODUCTS-COUNT>   Number of products to generate. Required.
 -o <OUTPUT-DIR>       Output directory for generated files. Required.
 -m <arg>              Elasticsearch Product and Product Model Index name.
                       Defaults to 'akeneo_pim_product_and_product_model'.
 -p <arg>              Elasticsearch Product Index name. Defaults to
                       'akeneo_pim_product'.
```
 - example:
```bash
$ java -jar akeneo-product-generator-full-0.3.0.jar -u="jdbc:mysql://localhost/pim_ce_20?user=akeneo_pim&password=akeneo_pim" -o=/var/tmp -c=1000000
```
It will generate 1 million products based on the structure from the local MySQL on the `pim_ce_20` database.

### Loading the generated TSV files into MySQL
```sql
mysql> LOAD DATA LOCAL INFILE '/var/tmp/products.tsv' INTO TABLE pim_catalog_product;
mysql> LOAD DATA LOCAL INFILE '/var/tmp/products-categories.tsv' INTO TABLE pim_catalog_category_product;
mysql> LOAD DATA LOCAL INFILE '/var/tmp/products-unique-data.tsv' INTO TABLE pim_catalog_product_unique_data;
```

### Loading generated JSON into Elasticsearch
```bash
for ES_FILE in /var/tmp/es/*.gzip; do
    echo "Loading $ES_FILE..."
    gunzip -c $ES_FILE | curl -s -H "Content-Type: application/x-ndjson" -XPOST localhost:9200/_bulk --data-binary @- | cut -b 1-50
done
```

## Troubleshooting:

`ERROR 1206 (HY000): The total number of locks exceeds the lock table size`
If you meet this error during the `LOAD DATA` commands (especially category associations and unique data),
you need to increase the `innodb_buffer_pool_size` for you MySQL server.

## Building

From the root directory:

```bash
$ gradle build
```

## Performances
Some metrics for 1 million products from the icecat_demo_dev structure:
 - Generating data files: 64s
 - Importing into the pim_catalog_product table: 2 min 30s
 - Importing into the pim_catalog_category_product table: 1 min 25s
 - Importing into pim_catalog_product_unique_data: 33s

 => full process from 0 product to 1 million products in DB: 5 min 30s

