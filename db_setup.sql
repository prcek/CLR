
-- --------------------------------------------------------

-- 
-- Table structure for table `data_change`
-- 

CREATE TABLE `data_change` (
  `key` varchar(32) NOT NULL default '',
  `last` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

-- 
-- Table structure for table `doktori`
-- 

CREATE TABLE `doktori` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(20) collate latin2_czech_cs default NULL,
  `surname` varchar(20) collate latin2_czech_cs default NULL,
  `jmeno` varchar(60) character set utf8 collate utf8_czech_ci NOT NULL default '',
  `aktivni` int(11) NOT NULL default '1',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin2 COLLATE=latin2_czech_cs;

-- --------------------------------------------------------

-- 
-- Table structure for table `klienti`
-- 

CREATE TABLE `klienti` (
  `id` bigint(20) NOT NULL auto_increment,
  `number` varchar(12) collate latin2_czech_cs NOT NULL default '',
  `name` varchar(32) collate latin2_czech_cs NOT NULL default '',
  `surname` varchar(64) collate latin2_czech_cs NOT NULL default '',
  `phone` varchar(32) collate latin2_czech_cs NOT NULL default '',
  `email` varchar(128) collate latin2_czech_cs NOT NULL default '',
  `street` varchar(64) collate latin2_czech_cs NOT NULL default '',
  `street_no` varchar(10) collate latin2_czech_cs NOT NULL default '',
  `city` varchar(64) collate latin2_czech_cs NOT NULL default '',
  `created` datetime NOT NULL default '0000-00-00 00:00:00',
  `deleted` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `cislo` (`number`)
) ENGINE=MyISAM DEFAULT CHARSET=latin2 COLLATE=latin2_czech_cs;

-- --------------------------------------------------------

-- 
-- Table structure for table `lekce`
-- 

CREATE TABLE `lekce` (
  `id` bigint(20) NOT NULL auto_increment,
  `date` datetime NOT NULL default '0000-00-00 00:00:00',
  `capacity` int(11) NOT NULL default '0',
  `reserve` int(11) NOT NULL default '0',
  `typ` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `date` (`date`),
  KEY `typ` (`typ`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

-- 
-- Table structure for table `lekce_jmena`
-- 

CREATE TABLE `lekce_jmena` (
  `id` int(11) NOT NULL default '0',
  `nazev` varchar(32) character set utf8 collate utf8_czech_ci NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

-- 
-- Table structure for table `masaze`
-- 

CREATE TABLE `masaze` (
  `id` bigint(20) NOT NULL auto_increment,
  `zacatek` datetime NOT NULL default '0000-00-00 00:00:00',
  `konec` datetime NOT NULL default '0000-00-00 00:00:00',
  `typ` int(11) NOT NULL default '0',
  `member_id` bigint(11) NOT NULL default '0',
  `telefon` varchar(32) collate utf8_czech_ci NOT NULL default '',
  `prijmeni` varchar(64) collate utf8_czech_ci NOT NULL default '',
  `popis` varchar(64) collate utf8_czech_ci NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `zacatek` (`zacatek`),
  KEY `konec` (`konec`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

-- 
-- Table structure for table `masaze_plan`
-- 

CREATE TABLE `masaze_plan` (
  `id` bigint(20) NOT NULL auto_increment,
  `zacatek` datetime NOT NULL default '0000-00-00 00:00:00',
  `konec` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`),
  KEY `zacatek` (`zacatek`),
  KEY `konec` (`konec`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

-- 
-- Table structure for table `masaze_typy`
-- 

CREATE TABLE `masaze_typy` (
  `id` bigint(20) NOT NULL default '0',
  `nazev` varchar(32) collate utf8_czech_ci NOT NULL default '',
  `delka` int(11) NOT NULL default '1',
  `kameny` int(11) NOT NULL default '0',
  `aktivni` int(11) NOT NULL default '1',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci PACK_KEYS=0;

-- --------------------------------------------------------

-- 
-- Table structure for table `permanentky`
-- 

CREATE TABLE `permanentky` (
  `id` bigint(20) NOT NULL auto_increment,
  `doctor_id` int(11) NOT NULL default '0',
  `type_id` int(11) NOT NULL default '0',
  `count` int(11) NOT NULL default '0',
  `cost` int(11) NOT NULL default '0',
  `type` varchar(64) collate utf8_czech_ci NOT NULL default '-',
  `user` varchar(64) collate utf8_czech_ci NOT NULL default '',
  `created` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`),
  KEY `doktor_id` (`doctor_id`),
  KEY `co_id` (`type_id`),
  KEY `kdy` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

-- 
-- Table structure for table `permanentky_co`
-- 

CREATE TABLE `permanentky_co` (
  `id` bigint(20) NOT NULL auto_increment,
  `nazev` varchar(64) collate utf8_czech_ci NOT NULL default '',
  `aktivni` int(11) NOT NULL default '1',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;


-- --------------------------------------------------------

-- 
-- Table structure for table `svatky`
-- 

CREATE TABLE `svatky` (
  `datum` date NOT NULL default '0000-00-00',
  `popis` varchar(30) collate latin2_czech_cs NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin2 COLLATE=latin2_czech_cs;

-- --------------------------------------------------------

-- 
-- Table structure for table `template`
-- 

CREATE TABLE `template` (
  `doc_id` int(10) unsigned NOT NULL default '0',
  `day` tinyint(3) unsigned NOT NULL default '0',
  `from` mediumint(8) unsigned NOT NULL default '0',
  `to` mediumint(8) unsigned NOT NULL default '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin2 COLLATE=latin2_czech_cs;

-- --------------------------------------------------------

-- 
-- Table structure for table `uzivatele`
-- 

CREATE TABLE `uzivatele` (
  `login` varchar(20) collate latin2_czech_cs NOT NULL default '',
  `password` varchar(20) collate latin2_czech_cs NOT NULL default '',
  `role` smallint(5) unsigned NOT NULL default '0',
  `selector` smallint(6) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin2 COLLATE=latin2_czech_cs;

-- --------------------------------------------------------

-- 
-- Table structure for table `zapis`
-- 

CREATE TABLE `zapis` (
  `id` bigint(20) NOT NULL auto_increment,
  `lekce_id` bigint(20) NOT NULL default '0',
  `klient_id` bigint(20) NOT NULL default '0',
  `date` datetime NOT NULL default '0000-00-00 00:00:00',
  `attend` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `lekce_id` (`lekce_id`),
  KEY `date` (`date`),
  KEY `klient_id` (`klient_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;
        


