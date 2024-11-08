-- leek_meta.meta_calendar definition
use leek_meta;

DROP TABLE IF EXISTS `meta_calendar`;
CREATE TABLE IF NOT EXISTS `meta_calendar` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `finance_type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `market_code` varchar(4) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_calendar_date_marketCode` (`date`,`market_code`,`finance_type`)
) ENGINE=InnoDB AUTO_INCREMENT=25786 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- leek_meta.meta_creeper definition

DROP TABLE IF EXISTS `meta_creeper`;
CREATE TABLE IF NOT EXISTS `meta_creeper` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` time NOT NULL,
  `data_type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `finance_type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `market_code` varchar(4) COLLATE utf8mb4_bin NOT NULL,
  `source` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `source_name` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `status` int NOT NULL,
  `updated_at` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_creeper` (`market_code`,`finance_type`,`data_type`,`source_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- leek_meta.meta_dict definition

DROP TABLE IF EXISTS `meta_dict`;
CREATE TABLE IF NOT EXISTS `meta_dict` (
  `code` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `created_at` time NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `parent_code` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `root_code` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `status` int NOT NULL,
  `updated_at` time DEFAULT NULL,
  `value` varchar(128) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;