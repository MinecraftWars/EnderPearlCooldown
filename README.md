Ender Pearl Cooldown
====================

Simple Bukkit plugin to add a cooldown or cost to ender pearls.

Features
--------
* add cooldown to ender pearls
* disable ender pearls completely on a per-user basis (permission)
* charge money for throwing pearls
* customizable messages

Installation
------------
Drop the jar in your craftbukkit/plugins folder. Per default, enderpearls will have a cooldown of 3 seconds.

Configuration
-------------
Example `plugin.yml` (default settings):

    cooldown: 3000
    price: 0
    showMessage: true

    messages:
      money: "§cNot enough money to throw pearl. Need at least {price}."
      cooldown: "§cEnderpearl cooldown remaining: {seconds} seconds."
      notallowed: "§cYou may not use ender pearls."

* `cooldown` is the time in milliseconds that players need to wait before throwing pearls. 
* `price` is how much each pearl throw costs
* `showMessage` determines whether users get feedback on unsuccessful throws or it just silently fails.
* `messages` are the individual messages sent to players when pearl throws fail.

Permissions
-----------

    enderpearl.cooldown:
      default: true

Ender pearl cooldown applies to players with this permission.

    enderpearl.use:
      default: true

Ender pearls may be used by players with this permission.

    enderpearl.pay:
      default: true

Players with this permission pay for each pearl throw.

    enderpearl.admin:
      default: op

Use admin command /epc reload