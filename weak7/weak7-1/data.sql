create database db01 default charset = 'UTF8';
use db01;
create table `tb_seller` (
	`sellerid` varchar (100),
	`name` varchar (100),
	`nickname` varchar (50),
	`password` varchar (60),
	`status` varchar (1),
	`address` varchar (100),
	`createtime` datetime,
    primary key(`sellerid`)
)engine=innodb default charset=utf8mb4; 

insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('alibaba','����Ͱ�','����С��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('baidu','�ٶȿƼ����޹�˾','�ٶ�С��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('huawei','��Ϊ�Ƽ����޹�˾','��ΪС��','e10adc3949ba59abbe56e057f20f883e','0','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('itcast','���ǲ��ͽ����Ƽ����޹�˾','���ǲ���','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('itheima','�������Ա','�������Ա','e10adc3949ba59abbe56e057f20f883e','0','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('luoji','�޼��Ƽ����޹�˾','�޼�С��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('oppo','OPPO�Ƽ����޹�˾','OPPO�ٷ��콢��','e10adc3949ba59abbe56e057f20f883e','0','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('ourpalm','��Ȥ�Ƽ��ɷ����޹�˾','��ȤС��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('qiandu','ǧ�ȿƼ�','ǧ��С��','e10adc3949ba59abbe56e057f20f883e','2','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('sina','���˿Ƽ����޹�˾','���˹ٷ��콢��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('xiaomi','С�׿Ƽ�','С�׹ٷ��콢��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');
insert into `tb_seller` (`sellerid`, `name`, `nickname`, `password`, `status`, `address`, `createtime`) values('yijia','�˼ҼҾ�','�˼ҼҾ��콢��','e10adc3949ba59abbe56e057f20f883e','1','������','2088-01-01 12:00:00');





CREATE TABLE `tb_sku` (
  `id` varchar(20) NOT NULL COMMENT '��Ʒid',
  `sn` varchar(100) NOT NULL COMMENT '��Ʒ����',
  `name` varchar(200) NOT NULL COMMENT 'SKU����',
  `price` int(20) NOT NULL COMMENT '�۸񣨷֣�',
  `num` int(10) NOT NULL COMMENT '�������',
  `alert_num` int(11) DEFAULT NULL COMMENT '���Ԥ������',
  `image` varchar(200) DEFAULT NULL COMMENT '��ƷͼƬ',
  `images` varchar(2000) DEFAULT NULL COMMENT '��ƷͼƬ�б�',
  `weight` int(11) DEFAULT NULL COMMENT '�������ˣ�',
  `create_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `update_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `spu_id` varchar(20) DEFAULT NULL COMMENT 'SPUID',
  `category_id` int(10) DEFAULT NULL COMMENT '��ĿID',
  `category_name` varchar(200) DEFAULT NULL COMMENT '��Ŀ����',
  `brand_name` varchar(100) DEFAULT NULL COMMENT 'Ʒ������',
  `spec` varchar(200) DEFAULT NULL COMMENT '���',
  `sale_num` int(11) DEFAULT '0' COMMENT '����',
  `comment_num` int(11) DEFAULT '0' COMMENT '������',
  `status` char(1) DEFAULT '1' COMMENT '��Ʒ״̬ 1-������2-�¼ܣ�3-ɾ��',
  `version` int(255) DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `cid` (`category_id`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `updated` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='��Ʒ��' 