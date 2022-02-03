package com.github.julyss2019.mcsp.julyguild.guild.member;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GuildMemberSign {
	private GuildMember guildMember;
	private ConfigurationSection section;
	private long lastSign;
	private List<Long> signHistories = new ArrayList<>();

	public GuildMemberSign(@NotNull GuildMember guildMember) {
		this.guildMember = guildMember;

		load();
	}

	private void load() {
		if (!guildMember.getSection().contains("sign")) {
			guildMember.getSection().createSection("sign");
		}

		this.section = guildMember.getSection().getConfigurationSection("sign");
		this.signHistories = section.getLongList("sign_histories");
		this.lastSign = guildMember.getGuildPlayer().getYaml().getLong("last_sign");
	}

	public void signToday() {
		if (isSignedToday()) {
			throw new RuntimeException("今日已签到");
		}

		long time = System.currentTimeMillis();

		setLastSign(time);
		save();
		addSignHistory(time);
	}

	public boolean isSignedToday() {
		return DateUtils.isSameDay(new Date(getLastSign()), new Date(System.currentTimeMillis()));
	}

	public int getSignedCount() {
		return signHistories.size();
	}

	public Set<Long> getSignHistories() {
		return new HashSet<>(signHistories);
	}

	private void addSignHistory(long l) {
		List<Long> tmp = new ArrayList<>(signHistories);

		tmp.add(l);
		section.set("sign_histories", tmp);
		save();
		this.signHistories.add(l);
	}

	/**
	 * 必须存到 GuildPlayer 里，不然可能会刷物品
	 * @param l
	 */
	private void setLastSign(long l) {
		guildMember.getGuildPlayer().getYaml().set("last_sign", l);
		guildMember.getGuildPlayer().save();
		this.lastSign = l;
	}

	public long getLastSign() {
		return lastSign;
	}

	public void save() {
		guildMember.save();
	}
}
