package de.sakros.civilization.utility;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collection;

import static de.sakros.civilization.utility.LuckPermsUtility.GroupName.*;

public class LuckPermsUtility {

    private static LuckPerms api;

    private static boolean getInstance() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            api = provider.getProvider();
            return true;
        }
        return false;
    }

    public static String getPlayerGroup(String pName) {
        if (getInstance()) {
            return getUser(pName).getPrimaryGroup();
        }
        return "";
    }

    private static User getUser(String pName) {
        if (getInstance())
            return api.getUserManager().getUser(pName);
        return null;
    }

    public static void setGroup(String group, String pName) {

        Node node = Node.builder("group." + group).build();
        getUser(pName).data().add(node);
        getUser(pName).setPrimaryGroup(group);
        api.getUserManager().saveUser(getUser(pName));
    }

    public static void addPerm(String perm, String pName) {

        PermissionNode node = PermissionNode.builder(perm).build();
        getUser(pName).data().add(node);
        api.getUserManager().saveUser(getUser(pName));
    }

    public static void removeGroups(String pName) {
        Collection<Node> nodes = getUser(pName).getNodes();
        ArrayList<Node> groups = new ArrayList<>();
        for (Node n : nodes) {
            String key = n.getKey();
            System.out.println(key);
            if (key.equalsIgnoreCase("group." + GroupName.BEWOHNER.getName()) || key.equalsIgnoreCase("group." + LVL1RITTER.getName()) || key.equalsIgnoreCase("group." + LVL2RITTER.getName()) || key.equalsIgnoreCase("group." + LVL3RITTER.getName())) {
                System.out.println("got");
                groups.add(n);
            }
        }
        for (Node n : groups) {
            nodes.remove(n);
        }
        api.getUserManager().saveUser(getUser(pName));
    }

    public enum GroupName {
        BEWOHNER("bewohner"),
        HELFER("helfer"),
        LVL1RITTER("lvl1ritter"),
        LVL2RITTER("lvl2ritter"),
        LVL3RITTER("lvl3ritter"),
        MODERATOR("moderator"),
        QUIIN("quiin"),
        VIP("vip"),
        ADELIGE("adelige"),
        ERZLORD("erzlord"),
        GENERAL("general"),
        GROSSMEISTER("grossmeister"),
        HEILIGE("heilige"),
        KNECHT("knecht"),
        LEIBWAECHTER("leibwächter"),
        LORD("lord"),
        SOLDAT("soldat"),
        TEAM("team"),
        DEFAULT("default");

        private String name;

        GroupName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
