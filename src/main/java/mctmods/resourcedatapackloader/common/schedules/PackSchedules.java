package mctmods.resourcedatapackloader.common.schedules;

import mctmods.resourcedatapackloader.common.pack.definitions.schedules.ScheduleDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;

public final class PackSchedules {
    private static final long DONE = Long.MAX_VALUE;
    private static final List<String> SCHEDULE_KEYS = List.of("runs", "after", "every");
    private static final List<ScheduleDef> SCHEDULES = new ArrayList<>();
    private static long[] due = new long[0];
    private static long next = DONE;
    private static long clock;

    private PackSchedules() {}

    public static void load() {
        SCHEDULES.clear();
        Json.eachObject(PackManager.SCHEDULES, "schedule file", PackSchedules::read);
        restart();
        if (!SCHEDULES.isEmpty()) { Summary.info("schedules", "Loaded " + SCHEDULES.size() + " schedule(s)"); }
    }

    public static void restart() {
        clock = 0;
        due = new long[SCHEDULES.size()];
        next = DONE;
        for (int i = 0; i < due.length; i++) {
            due[i] = SCHEDULES.get(i).after();
            next = Math.min(next, due[i]);
        }
    }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SCHEDULES.isEmpty() || ++clock < next) { return; }
        MinecraftServer server = event.getServer();
        next = DONE;
        for (int i = 0; i < due.length; i++) {
            ScheduleDef schedule = SCHEDULES.get(i);
            if (due[i] <= clock) {
                due[i] = schedule.every() > 0 ? clock + schedule.every() : DONE;
                Functions.run(server, schedule.runs(), "A schedule");
            }
            next = Math.min(next, due[i]);
        }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", SCHEDULE_KEYS);
        String runs = Json.getString(json, Json.RUNS, "");
        if (runs.isEmpty()) {
            ContentLog.LOGGER.error("Schedule file {} names no function to run, ignoring it", key);
            return;
        }
        SCHEDULES.add(new ScheduleDef(Math.max(0, Json.getInt(json, "after", 0)), Math.max(0, Json.getInt(json, "every", 0)), runs));
    }
}
