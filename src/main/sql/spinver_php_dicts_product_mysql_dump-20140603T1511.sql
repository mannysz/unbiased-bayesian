-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: spinver
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id_product` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `url` varchar(100) NOT NULL,
  `keywords` text NOT NULL,
  PRIMARY KEY (`id_product`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Iphone','iphone','iphone,apple iphone,iphone3,iphone4,iphone5,iphones,iphone4s,iphonea,iphone5c,5c,4s,3gs'),(2,'Galaxy','galaxy','s3,s4,sa[mn]su[mn]g galax[yi],galax[yi] s3,galax[yi] s4,s4mini,s3mini,galax[yi]'),(3,'Play Station','play-station','play 2,play 3,play 4,play station,play station 2,play station 3,play station 4,play ii,play iii,play station,play station ii,play station iii,playstation,playstation ?2,playstation ?3,ps2,ps3,ps4'),(4,'Xbox','xbox','xbox,xbox360,xboxone,xbox 360,xbox one,(console|video ?game) ?xbox'),(5,'Biz','biz','biz,honda biz,honda bis,moto biz,moto bis'),(6,'Frigobar','frigobar','frigobar'),(7,'BlackBerry','blackberry','blackberry,blackbery,blakberry,blacbery'),(8,'Gol','gol','gol'),(9,'Golf','golf','golf'),(10,'Ipod','ipod','ipod'),(11,'Ipad','ipad','ipad,ipad2,ipad3,ipadair,ipad air,ipadmini,ipad mini'),(12,'Carabina de pressão','carabina-pressao','carabina');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_model`
--

DROP TABLE IF EXISTS `product_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_model` (
  `id_product_model` int(11) NOT NULL AUTO_INCREMENT,
  `id_product` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` enum('color','size') NOT NULL,
  `keywords` text NOT NULL,
  PRIMARY KEY (`id_product_model`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_model`
--

LOCK TABLES `product_model` WRITE;
/*!40000 ALTER TABLE `product_model` DISABLE KEYS */;
INSERT INTO `product_model` VALUES (1,1,'Branco','color','branco,white'),(2,1,'Gold','color','ouro,gold'),(3,1,'Cinza espacial','color','cinza espacial,cinsa espacial'),(4,1,'Cinza','color','cinza,cinsa'),(5,1,'32gb','size','32g,32gb,32 g,32 gb'),(6,1,'16gb','size','16g,16gb,16 g,16 gb'),(7,1,'64gb','size','64g,64gb,64 g,64 gb'),(8,1,'8gb','size','8g,8gb,8 g,8 gb'),(9,1,'Preto','color','preto,negro,black'),(10,3,'Slim','size','slim,fino'),(11,4,'Slim','size','slim,fino,slin'),(12,5,'Vermelha','color','vermelho,vermelha'),(13,5,'Preta','color','preto,preta,black'),(14,5,'Branca','color','branco,branca'),(15,6,'1300','size','1300'),(16,10,'1GB','size','1gb,1 gb,1g,1 g'),(17,10,'2GB','size','2gb,2 gb,2g,2 g'),(18,10,'4gb','size','4gb,4 gb,4g,4 g'),(19,10,'8GB','size','8gb,8 gb,8g,8 g'),(20,10,'16GB','size','16gb,16 gb,16g,16 g'),(21,10,'30GB','size','30gb,30 gb,30g,30 g'),(22,10,'32GB','size','32gb,32 gb,32g,32 g'),(23,10,'64GB','size','64gb,64 gb,64g,64 g');
/*!40000 ALTER TABLE `product_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_version`
--

DROP TABLE IF EXISTS `product_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_version` (
  `id_product_version` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `id_product` int(11) NOT NULL,
  `keywords` text NOT NULL,
  `url` varchar(100) NOT NULL,
  PRIMARY KEY (`id_product_version`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_version`
--

LOCK TABLES `product_version` WRITE;
/*!40000 ALTER TABLE `product_version` DISABLE KEYS */;
INSERT INTO `product_version` VALUES (1,'3',1,'3','3'),(2,'3s',1,'3s','3s'),(3,'4',1,'4','4'),(4,'4s',1,'4s','4s'),(5,'5',1,'5','5'),(6,'5s',1,'5s','5s'),(7,'3gs',1,'3gs','3gs'),(8,'S3 mini',2,'s3 mini,siii mini','s3-mini'),(9,'s3',2,'s3,siii','s3'),(10,'S4',2,'s4','s4'),(11,'Ace',2,'ace','ace'),(12,'2',3,'2','2'),(13,'3',3,'3','3'),(14,'4',3,'4','4'),(15,'360',4,'360','360'),(16,'one',4,'one','one'),(17,'EX',5,'ex','ex'),(18,'ES',4,'es','es'),(19,'Crypton',5,'crypton','crypton'),(20,'100',5,'100,c100','100'),(21,'125',5,'125','125'),(22,'2001',5,'2001','2001'),(23,'2002',5,'2002','2002'),(24,'2003',5,'200','2003'),(25,'2004',4,'2004','2004'),(26,'2005',5,'2005','2005'),(27,'2008',5,'2008','2008'),(28,'2009',5,'2009','2009'),(29,'LG',6,'lg','lg'),(30,'Consul',6,'consul','consul'),(31,'Curve',7,'curve','curve'),(32,'Torch',7,'torch','torch'),(33,'Bold',7,'bold','bold'),(34,'Curve',7,'curve','curve'),(35,'Torch',7,'torch','torch'),(36,'Bold',7,'bold','bold'),(37,'G3',8,'g3','g3'),(38,'G5',8,'g5','g5'),(39,'Gl',8,'gl','gl'),(40,'96',8,'96','96'),(41,'Pauwer',8,'pauwer',''),(42,'88',8,'88',''),(43,'Cl',8,'cl',''),(44,'Cli',8,'cli',''),(45,'Tsi',8,'tsi',''),(46,'A3',9,'a3',''),(47,'gti',9,'gti',''),(48,'Sportline',9,'sportline',''),(49,'Nano',10,'nano',''),(50,'Touch',10,'touch',''),(51,'Mini',10,'mini',''),(52,'Shuffle',10,'shuffle',''),(53,'Classic',10,'classic',''),(54,'5c',1,'5c','5c'),(55,'2',11,'2','2'),(56,'3',11,'3','3'),(57,'4',11,'4','4'),(58,'Mini',11,'mini','mini'),(59,'Air',11,'air','air');
/*!40000 ALTER TABLE `product_version` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-06-03  9:07:55
