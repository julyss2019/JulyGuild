# JulyGuild

> 本贴使用 [MCBBS Markdown To BBCode Converter](https://mm2bc.ustc-zzzz.net/) 生成

## 为什么使用 JulyGuild

* 超过 170个 服务器（1200+个玩家）正在使用本插件
* 使用 GNU 协议开源
* 高度自定义
* 功能齐全
* 全局 GUI 支持
* 多版本支持
* 多服务端支持
* 长期更新

## 功能

![](https://s2.ax1x.com/2020/02/18/3FqyEn.gif)

### 创建公会

插件支持使用点券和金币来创建公会。

![1338159e-b642-49f3-aa53-dce2e1c77172.gif](https://i.loli.net/2020/03/16/QfCT7iNhc5aJkqr.gif)

### 加入公会

玩家只需要点几下鼠标就可以申请加入他人的公会。

### 公会信息

### 个人信息

### 公会排行榜

你可以自定义权重来指定排行榜。

### 入会审批

玩家可在 GUI 内申请加入公会，管理员只需要点下鼠标就可以处理请求。

### 成员管理

你可以踢出玩家，给予玩家各种权限，只需要点下鼠标。

###### ![3FXJE9.gif](https://s2.ax1x.com/2020/02/18/3FXJE9.gif)

### 捐赠

玩家可以为公会捐赠公会币，公会币是公会商店的交易货币。

![3FqTER.gif](https://s2.ax1x.com/2020/02/18/3FqTER.gif)

### 公会商店

玩家可以在公会商店购买图标，全员集结令，升级公会……

#### 公会主城

玩家可以为公会设置一个主城来供成员传送。

#### 公会升级

玩家可以使用公会币来升级公会的最大人数。

#### 公会图标

玩家可以使用公会币来为公会购买图标，图标会显示在主界面上。

你可以自己配置供销售的图标。

[![3FLoQS.gif](https://s2.ax1x.com/2020/02/18/3FLoQS.gif)](https://imgchr.com/i/3FLoQS)

#### 全员集结令

![3FO7p6.gif](https://s2.ax1x.com/2020/02/18/3FO7p6.gif)

### 成员免伤

玩家可以开关成员免伤，开启后免疫成员伤害。

### 图标仓库

玩家可以在图标仓库自由设置购买到的图标。

### 每日签到

玩家可以每日签到获取奖励。

## 高度自定义

### 配置自定义

你可以自定义插件里的许多参数：

* 创建公会的价格
* 最大成员数
* 排行榜计算公式
* ...

### 语言自定义

你可以自定义插件中的每条消息。

### GUI自定义

你可以自定义界面的标题，行数。

你也可以自定义物品的：

* 位置
* 材质
* 子ID
* 展示名
* skulTexture
* lore
* flag
* ...

#### 案例展示

这是一个没有修改过的默认主界面GUI：

![Snipaste_2020-03-16_13-04-24.png](https://i.loli.net/2020/03/16/XmnTC8zEkKD2UoO.png)

修改后：

![Snipaste_2020-03-16_13-03-38.png](https://i.loli.net/2020/03/16/F5Cy4VXDkLuzGfA.png)

方形：

![Snipaste_2020-03-16_13-12-17.png](https://i.loli.net/2020/03/16/2WvBZ5EqMPzgfHj.png)

心型：

![Snipaste_2020-03-16_13-08-30.png](https://i.loli.net/2020/03/16/hYiI29dJvxTRKaD.png)

你可以像这样自定义每个GUI，每个物品。

## 指令与权限（部分）

### /jguild gui main

打开主界面。

**(需要权限：JulyGuild.use)**

### /jguild plugin reload

重载插件配置。

**(需要权限：JulyGuild.admin)**

### /jguild plugin version

显示插件信息。

## PlaceholderAPI 变量（部分）

| **变量名**                    | **返回**               |
| ----------------------------- | ---------------------- |
| %guild_name%                  | 公会名                 |
| %guild_member_position%       | 职位：成员，主人       |
| %guild_member_donated_gmoney% | 成员已赞助的公会币数量 |
| %guild_member_join_time%      | 成员加入时间           |
| %guild_ranking%               | 公会排名               |
| %guild_owner%                 | 公会主人               |
| %guild_member_count%          | 公会成员数量           |
| %guild_max_member_count%      | 公会最大成员数量       |
| %guild_creation_time%         | 公会创建时间           |
| %guild_bank_gmoney%           | 公会币储备             |
| %guild_online_member_count%   | 公会在线成员数         |

## 常见问题

[Wiki](https://github.com/julyss2019/JulyGuild/wiki)

[完整的指令？](https://github.com/julyss2019/JulyGuild/wiki)

[PlaceholderAPI 变量？](https://github.com/julyss2019/JulyGuild/wiki/PlaceholderAPI变量)

[怎么安装？](https://github.com/julyss2019/JulyGuild/wiki/安装)

[怎么配置配置文件？](https://github.com/julyss2019/JulyGuild/wiki/配置)

[怎么使用 TrChat 在聊天中显示公会名？](https://github.com/julyss2019/JulyGuild/wiki/常见问题)

[怎么在 Essentials 聊天中显示公会名？](https://github.com/julyss2019/JulyGuild/wiki/常见问题)

[Vault Hook 失败？](https://github.com/julyss2019/JulyGuild/wiki/常见问题)

[GUI 内变量不显示？](https://github.com/julyss2019/JulyGuild/wiki/常见问题)

## 源代码

[点击传送](https://github.com/julyss2019/JulyGuild/)

## 交流

插件交流/BUG反馈群：786184610。

## 支持作者

[点击传送到爱发电](https://afdian.net/@julyguild)

## 使用情况统计

![](https://bstats.org/signatures/bukkit/JulyGuild.svg)

## 下载

[hide]链接：https://pan.baidu.com/s/1qmSlpemcI4NT7CqDhkWlQw 提取码：v2ic[/hide]