package mctmods.resourcedatapackloader.content.card;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CardIds {
    public static final String GATE_UNLOCKED = "rdpl:gate_unlocked";
    public static final String GATE_BLOCKED = "rdpl:gate_blocked";
    public static final String TEAM_JOINED = "rdpl:team_joined";
    public static final String TEAM_LEAD = "rdpl:team_lead";
    public static final String TEAM_PICKED = "rdpl:team_picked";
    public static final String TEAM_ROUND_ENDED = "rdpl:team_round_ended";
    public static final String LOBBY_JOINS = "rdpl:lobby_joins";
    public static final String LOBBY_NOTE = "rdpl:lobby_note";
    public static final String SCORING_RESULTS = "rdpl:scoring_results";
    public static final String SCORING_OUT = "rdpl:scoring_out";
    public static final String RESET_LEAD = "rdpl:reset_lead";
    public static final String RESET_VOTE = "rdpl:reset_vote";
    public static final String RESET_PASS = "rdpl:reset_pass";
    public static final String RESET_FAIL = "rdpl:reset_fail";
    public static final String ANVIL_WAITS = "rdpl:anvil_waits";
    public static final String THREAT = "rdpl:threat";
    public static final String PROSPECT = "rdpl:prospect";
    public static final String PROSPECT_NONE = "rdpl:prospect_none";
    public static final String PREGEN_ENDED = "rdpl:pregen_ended";
    public static final String PREGEN_RUNNING = "rdpl:pregen_running";
    public static final List<String> ALL = Collections.unmodifiableList(Arrays.asList(GATE_UNLOCKED, GATE_BLOCKED, TEAM_JOINED, TEAM_LEAD, TEAM_PICKED, TEAM_ROUND_ENDED,
            LOBBY_JOINS, LOBBY_NOTE, SCORING_RESULTS, SCORING_OUT, RESET_LEAD, RESET_VOTE, RESET_PASS, RESET_FAIL, ANVIL_WAITS, THREAT, PROSPECT, PROSPECT_NONE, PREGEN_ENDED, PREGEN_RUNNING));

    private CardIds() {}
}
