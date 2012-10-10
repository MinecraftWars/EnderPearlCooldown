Ender Pearl Cooldown
====================

Simple Bukkit plugin to add a cooldown to ender pearls.

Features
--------
* add cooldown to ender pearls
* disable ender pearls completely on a per-user basis (permission)

Installation
------------
Drop the jar in your craftbukkit/plugins folder. Per default, enderpearls will have a cooldown of 3 seconds.

Configuration
-------------
Example `plugin.yml`:

    cooldown: 3000


That's all! The number here is the time in milliseconds that players need to wait before throwing pearls.

Permissions
-----------

    enderpearl.cooldown:
      default: true

Ender pearl cooldown applies to players with this permission.

    enderpearl.use:
      default: true

Ender pearls may be used by players with this permission

    enderpearl.admin:
      default: op

Use admin command /epc reload