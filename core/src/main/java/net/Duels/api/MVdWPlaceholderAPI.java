package net.Duels.api;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.Duels.Duel;
import net.Duels.api.impls.MVdWCoinAPI;
import net.Duels.api.impls.MVdWDeathAPI;
import net.Duels.api.impls.MVdWKillAPI;
import net.Duels.api.impls.MVdWKitSelectedAPI;
import net.Duels.api.impls.MVdWRankAPI;
import net.Duels.api.impls.MVdWScoreAPI;
import net.Duels.api.impls.MVdWWinAPI;
import net.Duels.api.impls.MVdWXpAPI;

public class MVdWPlaceholderAPI {
	
	public void register() {
		this.registerAPI("duels_rank", new MVdWRankAPI());
		this.registerAPI("duels_kills", new MVdWKillAPI());
		this.registerAPI("duels_wins", new MVdWWinAPI());
		this.registerAPI("duels_deaths", new MVdWDeathAPI());
		this.registerAPI("duels_score", new MVdWScoreAPI());
		this.registerAPI("duels_coins", new MVdWCoinAPI());
		this.registerAPI("duels_xp", new MVdWXpAPI());
		this.registerAPI("duels_kit", new MVdWKitSelectedAPI());
	}
	
	private void registerAPI(String name, PlaceholderReplacer replacer) {
		PlaceholderAPI.registerPlaceholder(Duel.getInstance(), name, replacer);
	}
	
}
