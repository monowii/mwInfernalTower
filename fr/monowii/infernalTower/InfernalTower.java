package fr.monowii.infernalTower;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InfernalTower extends JavaPlugin implements Listener
{
    Location lobby = null;
    Location spectateur = null;

    String gEditor = null;
    boolean gEdit = false;
    String ifEditor = null;
    boolean ifEdit = false;

    boolean Lobby = false;
    int LobbyTimer = 20;
    boolean GameInProgress = false;

    String Winner = "";

    int counter = 8;

    ArrayList<String> pPlay = new ArrayList<String>();
    ArrayList<String> pRunner = new ArrayList<String>();

    int xPos1 = 0;
    int yPos1 = 0;
    int zPos1 = 0;
    int xPos2 = 0;
    int yPos2 = 0;
    int zPos2 = 0;
    int yLevel = 0;

    int GxPos1 = 0;
    int GyPos1 = 0;
    int GzPos1 = 0;
    int GxPos2 = 0;
    int GyPos2 = 0;
    int GzPos2 = 0;
    int TaskWaterRise;
    int TaskPlayerCheck;
    int TaskLobby;

    public void onEnable()
    {
	getServer().getPluginManager().registerEvents(this, this);
	loadCfg();
    }

    
    public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args)
    {
	Player p = null;
	if (sender instanceof Player) p = (Player) sender;

	if ((cmd.getName().equalsIgnoreCase("infernaltower")) && (p != null) && ((p.isOp()) || (p.hasPermission("infernaltower.admin"))))
	{
	    if (args.length == 0)
	    {
		p.sendMessage("§8---=[ §2I§anfernal§2T§aowner §8]=---");
		p.sendMessage("/if edit <if/gate>  - Set the tower points");
		p.sendMessage("/if setLobby  - Set the lobby spawn");
		p.sendMessage("/if setSpec  - Set the spectateur spawn");
		p.sendMessage("/if list - Show the player in InfernalTower");
		p.sendMessage("/if removeall - Show the player in InfernalTower");
	    }
	    else
	    {
		if (args[0].equalsIgnoreCase("setLobby"))
		{
		    p.sendMessage("§aLobby Spawn set to your location !");

		    FileConfiguration cfg = getConfig();
		    cfg.set("lobby.xPos", Double.valueOf(p.getLocation().getX()));
		    cfg.set("lobby.yPos", Double.valueOf(p.getLocation().getY()));
		    cfg.set("lobby.zPos", Double.valueOf(p.getLocation().getZ()));
		    cfg.set("lobby.Pitch", Float.valueOf(p.getLocation().getPitch()));
		    cfg.set("lobby.Yaw", Float.valueOf(p.getLocation().getYaw()));
		    saveConfig();
		    loadCfg();
		}

		if (args[0].equalsIgnoreCase("setSpec"))
		{
		    p.sendMessage("§aSpectateur Spawn set to your location !");

		    FileConfiguration cfg = getConfig();
		    cfg.set("spectateur.xPos", Double.valueOf(p.getLocation().getX()));
		    cfg.set("spectateur.yPos", Double.valueOf(p.getLocation().getY()));
		    cfg.set("spectateur.zPos", Double.valueOf(p.getLocation().getZ()));
		    cfg.set("spectateur.Pitch", Float.valueOf(p.getLocation().getPitch()));
		    cfg.set("spectateur.Yaw", Float.valueOf(p.getLocation().getYaw()));
		    saveConfig();
		    loadCfg();
		}

		if (args[0].equalsIgnoreCase("list"))
		{
		    p.sendMessage("§8Players registered for InfernalTower :");
		    String pRegistered = "";
		    for (Player pl : getServer().getOnlinePlayers())
		    {
			if (pPlay.contains(pl.getName()))
			{
			    pRegistered = pRegistered + "/" + pl.getName();
			}
		    }
		    p.sendMessage("§7" + pRegistered);
		}

		if (args[0].equalsIgnoreCase("edit"))
		{
		    if (args.length == 2)
		    {
			if (args[1].equalsIgnoreCase("if"))
			{
			    if (ifEdit)
			    {
				p.sendMessage("IfernalTower edit mode §4OFF");
				ifEditor = null;
				ifEdit = false;
				loadCfg();
			    }
			    else if (gEdit)
			    {
				p.sendMessage("§cGate edit is already ON !");
			    }
			    else
			    {
				p.sendMessage("IfernalTower edit mode §aON");
				p.sendMessage("right/left click with stick to place the 2 points");
				ifEdit = true;
				ifEditor = p.getName();
			    }

			}
			else if (args[1].equalsIgnoreCase("gate"))
			{
			    if (gEdit)
			    {
				p.sendMessage("Gate edit mode §4OFF");
				gEditor = null;
				gEdit = false;
				loadCfg();
			    }
			    else if (ifEdit)
			    {
				p.sendMessage("§IfernalTower edit is already ON !");
			    }
			    else
			    {
				p.sendMessage("Gate edit mode §aON");
				p.sendMessage("right/left click with stick to place the 2 points");
				gEdit = true;
				gEditor = p.getName();
			    }
			}

		    }
		    else
		    {
			p.sendMessage("Bad args !");
		    }
		}
	    }
	}
	return false;
    }
    
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
	if (pPlay.contains(e.getPlayer().getName()))
	{
	    pPlay.remove(e.getPlayer().getName());
	}
	if (pRunner.contains(e.getPlayer().getName()))
	{
	    pRunner.remove(e.getPlayer().getName());
	}
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e)
    {
	if ((e.getPlayer().hasPermission("mwInfernalTower.admin")) || (e.getPlayer().isOp()))
	{
	    if (e.getLine(0).equalsIgnoreCase("[if]"))
	    {
		if (e.getLine(1).equalsIgnoreCase("join"))
		{
		    e.setLine(0, "[InfernalTower]");
		    e.setLine(1, "§bRejoindre");
		    e.setLine(2, "");
		    e.setLine(3, "");
		}
		else if (e.getLine(1).equalsIgnoreCase("quit"))
		{
		    e.setLine(0, "[InfernalTower]");
		    e.setLine(1, "§eQuitter");
		    e.setLine(2, "");
		    e.setLine(3, "");
		}
		else if (e.getLine(1).equalsIgnoreCase("newround"))
		{
		    e.setLine(0, "[InfernalTower]");
		    e.setLine(1, "§aNouvelle");
		    e.setLine(2, "§apartie");
		    e.setLine(3, "");
		}
		else if (e.getLine(1).equalsIgnoreCase("end"))
		{
		    e.setLine(0, "[InfernalTower]");
		    e.setLine(1, "§4Terminer");
		    e.setLine(2, "");
		    e.setLine(3, "");
		}
	    }
	}
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
	if (ifEdit)
	{
	    if ((e.getPlayer().getName() == ifEditor) && (e.getItem().getTypeId() == 280))
	    {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
		    e.getPlayer().sendMessage("§aInfernalTower Pos 1 Set !");
		    getConfig().set("tower.pos.xPos1", Double.valueOf(e.getClickedBlock().getLocation().getX()));
		    getConfig().set("tower.pos.yPos1", Double.valueOf(e.getClickedBlock().getLocation().getY()));
		    getConfig().set("tower.pos.zPos1", Double.valueOf(e.getClickedBlock().getLocation().getZ()));
		    saveConfig();

		    xPos1 = e.getClickedBlock().getLocation().getBlockX();
		    yPos1 = e.getClickedBlock().getLocation().getBlockY();
		    zPos1 = e.getClickedBlock().getLocation().getBlockZ();
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
		    e.getPlayer().sendMessage("§aInfernalTower Pos 2 Set !");
		    getConfig().set("tower.pos.xPos2", Double.valueOf(e.getClickedBlock().getLocation().getX()));
		    getConfig().set("tower.pos.yPos2", Double.valueOf(e.getClickedBlock().getLocation().getY()));
		    getConfig().set("tower.pos.zPos2", Double.valueOf(e.getClickedBlock().getLocation().getZ()));
		    saveConfig();

		    xPos2 = e.getClickedBlock().getLocation().getBlockX();
		    yPos2 = e.getClickedBlock().getLocation().getBlockY();
		    zPos2 = e.getClickedBlock().getLocation().getBlockZ();
		}
	    }
	} else if (gEdit)
	{
	    if ((e.getPlayer().getName() == gEditor) && (e.getItem().getTypeId() == 280))
	    {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
		    e.getPlayer().sendMessage("§aGate Pos 1 Set !");
		    getConfig().set("tower.gate.xPos1", Double.valueOf(e.getClickedBlock().getLocation().getX()));
		    getConfig().set("tower.gate.yPos1", Double.valueOf(e.getClickedBlock().getLocation().getY()));
		    getConfig().set("tower.gate.zPos1", Double.valueOf(e.getClickedBlock().getLocation().getZ()));
		    saveConfig();

		    GxPos1 = e.getClickedBlock().getLocation().getBlockX();
		    GyPos1 = e.getClickedBlock().getLocation().getBlockY();
		    GzPos1 = e.getClickedBlock().getLocation().getBlockZ();
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
		    e.getPlayer().sendMessage("§aGate Pos 2 Set !");
		    getConfig().set("tower.gate.xPos2", Double.valueOf(e.getClickedBlock().getLocation().getX()));
		    getConfig().set("tower.gate.yPos2", Double.valueOf(e.getClickedBlock().getLocation().getY()));
		    getConfig().set("tower.gate.zPos2", Double.valueOf(e.getClickedBlock().getLocation().getZ()));
		    saveConfig();

		    GxPos2 = e.getClickedBlock().getLocation().getBlockX();
		    GyPos2 = e.getClickedBlock().getLocation().getBlockY();
		    GzPos2 = e.getClickedBlock().getLocation().getBlockZ();
		}

	    }

	} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
	{
	    if (e.getClickedBlock().getState() instanceof Sign)
	    {
		Sign s = (Sign) e.getClickedBlock().getState();
		Player p = e.getPlayer();

		if ((s.getLine(0).equalsIgnoreCase("[InfernalTower]")) && (s.getLine(1).equalsIgnoreCase("§bRejoindre")))
		{
		    if (Lobby)
		    {
			p.sendMessage("§aVous avez rejoint la InfernalTower !");
			if (!pPlay.contains(p.getName()))
			    pPlay.add(p.getName());
			if (!pRunner.contains(p.getName()))
			    pRunner.add(p.getName());
			p.teleport(spectateur);
			say("§e" + p.getName() + " a rejoint la InfernalTower !");
			p.teleport(lobby);
		    }
		    if (!pPlay.contains(p.getName()))
		    {
			p.sendMessage("§aVous avez rejoint la InfernalTower !");
			pPlay.add(p.getName());
			p.teleport(spectateur);
			say("§e" + p.getName() + " a rejoint la InfernalTower !");
		    }
		}

		if ((s.getLine(0).equalsIgnoreCase("[InfernalTower]")) && (s.getLine(1).equalsIgnoreCase("§aNouvelle")) && (!GameInProgress) && (LobbyTimer > 0))
		{
		    if (pPlay.contains(p.getName()))
		    {
			if (Lobby)
			{
			    p.sendMessage("§cUne partie est deja en cour !");
			}
			else
			{
			    say("§4Nouvelle partie lancé par " + p.getName());
			    Lobby = true;
			    ClearWater();
			    Lobby();
			}
		    }
		    else
		    {
			p.sendMessage("§cVous n'avez pas rejoint la InfernalTower !");
		    }

		}

		if ((s.getLine(0).equalsIgnoreCase("[InfernalTower]")) && (s.getLine(1).equalsIgnoreCase("§eQuitter")))
		{
		    if (pPlay.contains(p.getName()))
		    {
			pPlay.remove(p.getName());

			if (pRunner.contains(p.getName()))
			{
			    pRunner.remove(p.getName());
			}
			p.sendMessage("§aVous avez quitter la InfernalTower !");

			say("§e" + p.getName() + " a quité la InfernalTower !");
		    }
		    else
		    {
			p.sendMessage("§cVous n'avez pas rejoint la InfernalTower !");
		    }

		}

		if ((s.getLine(0).equalsIgnoreCase("[InfernalTower]")) && (s.getLine(1).equalsIgnoreCase("§4Terminer")) && (GameInProgress) && (pRunner.contains(p.getName())))
		{
		    Winner = p.getName();
		    GameInProgress = false;

		    FillWater();

		    for (Player pl : getServer().getOnlinePlayers())
		    {
			if (pRunner.contains(pl.getName()))
			{
			    if (pl.getName() != Winner)
			    {
				pl.sendMessage("§bMiam miam miam !");
				say("§e" + pl.getName() + " est mort dans la InfernalTower !");
				pl.teleport(spectateur);
				pRunner.remove(pl.getName());
			    }
			}

		    }

		    say("§6" + Winner + " a gagné le round !");

		    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			    {
				public void run()
				{
				    getServer() .getPlayer(Winner).teleport(spectateur);
				    ResetIf();
				}
			    }, 160L);
		}
	    }
	}
    }
    
    
    public void WaterRise()
    {
	final World w = getServer().getWorld(getConfig().getString("tower.world"));

	final int xMin = Math.min(xPos1, xPos2);
	final int yMin = Math.min(yPos1, yPos2);
	final int zMin = Math.min(zPos1, zPos2);
	final int xMax = Math.max(xPos1, xPos2);
	final int yMax = Math.max(yPos1, yPos2);
	final int zMax = Math.max(zPos1, zPos2);

	yLevel = yMin;

	TaskWaterRise = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
		    public void run()
		    {
			int z;
			if ((yLevel >= yMin) && (yLevel <= yMax))
			{
			    for (int x = xMin; x <= xMax; x++)
			    {
				for (z = zMin; z <= zMax; z++)
				{
				    if (w.getBlockAt(x, yLevel, z).getType() == Material.AIR)
				    {
					w.getBlockAt(x, yLevel, z).setTypeId(8);
				    }
				}
			    }
			    yLevel += 1;
			}
			else if (counter != 0)
			{
			    counter -= 1;
			}
			else if (counter == 0)
			{
			    for (Player p : getServer().getOnlinePlayers())
			    {
				if (pRunner.contains(p.getName()))
				{
				    p.sendMessage("§bMiam miam miam !");
				    say("§e" + p.getName() + " est mort dans la InfernalTower !");
				    p.teleport(spectateur);
				    pRunner.remove(p.getName());
				}
			    }
			    say("§6InfernalTower a gagné cette partie !");
			    ResetIf();
			}
		    }
		}, 40L, 30L);
    }

    public void PlayerCheck()
    {
	TaskPlayerCheck = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
		    public void run()
		    {
			if (GameInProgress)
			{
			    for (Player p : getServer().getOnlinePlayers())
			    {
				if (pRunner.contains(p.getName()))
				{
				    if (p.getLocation().getY() < yLevel)
				    {
					p.sendMessage("§bMiam miam miam !");
					say("§e" + p.getName() + " est mort dans la InfernalTower !");
					p.teleport(spectateur);
					pRunner.remove(p.getName());
				    }
				}
			    }
			}

			if (pRunner.isEmpty())
			{
			    say("§6InfernalTower a gagné cette partie !");
			    ResetIf();
			}
		    }
		}, 0L, 20L);
    }

    public void Lobby()
    {
	TaskLobby = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
		    public void run()
		    {
			if (pPlay.isEmpty())
			{
			    Lobby = false;
			    getServer().getScheduler().cancelTask(TaskLobby);
			    ResetIf();
			}
			if (Lobby)
			{
			    String timer = "" + LobbyTimer;

			    if (LobbyTimer == 1)
			    {
				say("§8La partie commence dans §b1 seconde");
			    }
			    else if (LobbyTimer == 0)
			    {
				Lobby = false;
				say("§aGo Go Go !");
				ToggleGate(false);
				PlayerCheck();
				WaterRise();
				getServer().getScheduler().cancelTask(TaskLobby);
				GameInProgress = true;
			    }
			    else if (LobbyTimer == 20)
			    {
				ToggleGate(true);

				say("§8La partie commence dans §b" + timer + " secondes");

				for (String p : pPlay)
				{
				    if (!pRunner.contains(p))
				    {
					pRunner.add(p);
					getServer().getPlayer(p).teleport(lobby);
				    }
				}
			    }
			    else if (LobbyTimer == 15)
			    {
				say("§8La partie commence dans §b" + timer + " secondes");
			    }
			    else if (LobbyTimer <= 10)
			    {
				say("§8La partie commence dans §b" + timer + " secondes");
			    }
			    LobbyTimer -= 1;
			}
		    }
		}, 20L, 20L);
    }

    public void FillWater()
    {
	World w = getServer().getWorld(getConfig().getString("tower.world"));

	int xMin = Math.min(xPos1, xPos2);
	int yMin = Math.min(yPos1, yPos2);
	int zMin = Math.min(zPos1, zPos2);
	int xMax = Math.max(xPos1, xPos2);
	int yMax = Math.max(yPos1, yPos2);
	int zMax = Math.max(zPos1, zPos2);

	for (int x = xMin; x <= xMax; x++)
	{
	    for (int y = yMin; y <= yMax; y++)
	    {
		for (int z = zMin; z <= zMax; z++)
		{
		    if (w.getBlockAt(x, y, z).getType() == Material.AIR)
		    {
			w.getBlockAt(x, y, z).setTypeId(8);
		    }
		}
	    }
	}
    }

    public void ClearWater()
    {
	World w = getServer().getWorld(getConfig().getString("tower.world"));

	int xMin = Math.min(xPos1, xPos2);
	int yMin = Math.min(yPos1, yPos2);
	int zMin = Math.min(zPos1, zPos2);
	int xMax = Math.max(xPos1, xPos2);
	int yMax = Math.max(yPos1, yPos2);
	int zMax = Math.max(zPos1, zPos2);

	for (int x = xMin; x <= xMax; x++)
	{
	    for (int y = yMin; y <= yMax; y++)
	    {
		for (int z = zMin; z <= zMax; z++)
		{
		    if ((w.getBlockAt(x, y, z).getType() == Material.WATER) || (w.getBlockAt(x, y, z).getType() == Material.STATIONARY_WATER))
		    {
			w.getBlockAt(x, y, z).setTypeId(0);
		    }
		}
	    }
	}
    }

    public void ToggleGate(boolean state)
    {
	World w = getServer().getWorld(getConfig().getString("tower.world"));

	int xMin = Math.min(GxPos1, GxPos2);
	int yMin = Math.min(GyPos1, GyPos2);
	int zMin = Math.min(GzPos1, GzPos2);
	int xMax = Math.max(GxPos1, GxPos2);
	int yMax = Math.max(GyPos1, GyPos2);
	int zMax = Math.max(GzPos1, GzPos2);

	for (int x = xMin; x <= xMax; x++)
	{
	    for (int y = yMin; y <= yMax; y++)
	    {
		for (int z = zMin; z <= zMax; z++)
		{
		    if (state)
		    {
			w.getBlockAt(x, y, z).setTypeId(7);
		    } else
		    {
			w.getBlockAt(x, y, z).setTypeId(0);
		    }
		}
	    }
	}
    }

    public void ResetIf()
    {
	ClearWater();
	getServer().getScheduler().cancelAllTasks();
	Lobby = false;
	LobbyTimer = 20;
	GameInProgress = false;
	ToggleGate(true);
	pRunner.clear();
	Winner = null;
	counter = 8;
    }

    public void say(String message)
    {
	for (Player p : getServer().getOnlinePlayers())
	    if (pPlay.contains(p.getName()))
		p.sendMessage(message);
    }

    public void loadCfg()
    {
	FileConfiguration cfg = getConfig();

	cfg.addDefault("tower.world", "world");
	cfg.addDefault("tower.pos.xPos1", Integer.valueOf(0));
	cfg.addDefault("tower.pos.yPos1", Integer.valueOf(0));
	cfg.addDefault("tower.pos.zPos1", Integer.valueOf(0));
	cfg.addDefault("tower.pos.xPos2", Integer.valueOf(1));
	cfg.addDefault("tower.pos.yPos2", Integer.valueOf(1));
	cfg.addDefault("tower.pos.zPos2", Integer.valueOf(1));

	cfg.addDefault("tower.gate.xPos1", Integer.valueOf(2));
	cfg.addDefault("tower.gate.yPos1", Integer.valueOf(2));
	cfg.addDefault("tower.gate.zPos1", Integer.valueOf(2));
	cfg.addDefault("tower.gate.xPos2", Integer.valueOf(3));
	cfg.addDefault("tower.gate.yPos2", Integer.valueOf(3));
	cfg.addDefault("tower.gate.zPos2", Integer.valueOf(3));

	cfg.options().copyDefaults(true);

	saveConfig();

	spectateur = new Location(getServer().getWorld(
		cfg.getString("tower.world")),
		cfg.getDouble("spectateur.xPos"),
		cfg.getDouble("spectateur.yPos"),
		cfg.getDouble("spectateur.zPos"));
	spectateur.setPitch((float) cfg.getDouble("spectateur.Pitch"));
	spectateur.setYaw((float) cfg.getDouble("spectateur.Yaw"));

	lobby = new Location(getServer().getWorld(
		cfg.getString("tower.world")), cfg.getDouble("lobby.xPos"),
		cfg.getDouble("lobby.yPos"), cfg.getDouble("lobby.zPos"));
	lobby.setPitch((float) cfg.getDouble("lobby.Pitch"));
	lobby.setYaw((float) cfg.getDouble("lobby.Yaw"));

	xPos1 = cfg.getInt("tower.pos.xPos1");
	yPos1 = cfg.getInt("tower.pos.yPos1");
	zPos1 = cfg.getInt("tower.pos.zPos1");
	xPos2 = cfg.getInt("tower.pos.xPos2");
	yPos2 = cfg.getInt("tower.pos.yPos2");
	zPos2 = cfg.getInt("tower.pos.zPos2");

	GxPos1 = cfg.getInt("tower.gate.xPos1");
	GyPos1 = cfg.getInt("tower.gate.yPos1");
	GzPos1 = cfg.getInt("tower.gate.zPos1");
	GxPos2 = cfg.getInt("tower.gate.xPos2");
	GyPos2 = cfg.getInt("tower.gate.yPos2");
	GzPos2 = cfg.getInt("tower.gate.zPos2");
    }
}