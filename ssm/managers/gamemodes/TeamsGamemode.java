package ssm.managers.gamemodes;

import ssm.events.PlayerLostLifeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import ssm.managers.TeamManager;
import ssm.managers.TeamManager.TeamColor;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.teams.SmashTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsGamemode extends SmashGamemode {

    private int name_index = 0;
    private List<SmashTeam> teams = new ArrayList<SmashTeam>();
    private SmashTeam[] team_deaths = new SmashTeam[2];
    protected TeamColor current_team_data = TeamColor.first();
    public int players_per_team = 2;

    public TeamsGamemode() {
        super();
        this.name = "Super Smash Mobs Teams";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
        this.players_to_start = 3;
        this.max_players = 6;
    }

    // Even distribution of players across the teams
    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        int team_amount = (int) Math.ceil((float) server.getActivePlayerCount() / players_per_team);
        // Create teams
        for(SmashTeam team : teams) {
            TeamManager.removeTeam(team);
        }
        teams.clear();
        current_team_data = TeamColor.first();
        for(int i = 0; i < team_amount; i++) {
            teams.add(TeamManager.createTeam(current_team_data.names[name_index], current_team_data.color));
            current_team_data = current_team_data.next();
            if(current_team_data == null) {
                current_team_data = TeamColor.first();
                name_index++;
            }
        }
        // Evenly distribute players into teams
        int current_team_index = 0;
        for(Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            lives.put(player, 4);
            SmashTeam team = teams.get(current_team_index);
            team.addPlayer(player);
            current_team_index = (current_team_index + 1) % teams.size();
        }
    }

    public List<String> getLivesScoreboard() {
        List<String> scoreboard_string = new ArrayList<String>();
        for(SmashTeam team : teams) {
            for(Player add : team.getPlayersSortedByLives()) {
                scoreboard_string.add(server.getLives(add) + " " + SmashScoreboard.getPlayerColor(add, true) + add.getName());
            }
        }
        return scoreboard_string;
    }

    @Override
    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        int teams_left = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                teams_left++;
            }
        }
        return (teams_left <= 1);
    }

    @Override
    public String getFirstPlaceString() {
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                return team.getColor() + team.getName();
            }
        }
        return null;
    }

    @Override
    public String getSecondPlaceString() {
        int found_alive = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                found_alive++;
            }
            if(found_alive == 2) {
                return team.getColor() + team.getName();
            }
        }
        if(team_deaths[0] != null) {
            return team_deaths[0].getColor() + team_deaths[0].getName();
        }
        return null;
    }

    @Override
    public String getThirdPlaceString() {
        int found_alive = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                found_alive++;
            }
            if(found_alive == 3) {
                return team.getColor() + team.getName();
            }
        }
        if(team_deaths[1] != null) {
            return team_deaths[1].getColor() + team_deaths[1].getName();
        }
        return null;
    }

    @EventHandler
    public void onPlayerLostLife(PlayerLostLifeEvent e) {
        SmashTeam team = TeamManager.getPlayerTeam(e.getPlayer());
        if(team == null) {
            return;
        }
        if(!team.hasAliveMembers()) {
            team_deaths[1] = team_deaths[0];
            team_deaths[0] = team;
        }
    }

}