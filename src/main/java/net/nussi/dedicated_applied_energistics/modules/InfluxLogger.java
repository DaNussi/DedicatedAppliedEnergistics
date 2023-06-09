package net.nussi.dedicated_applied_energistics.modules;

import appeng.api.stacks.AEKey;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.Run;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import net.nussi.dedicated_applied_energistics.DedicatedAppliedEnergisticsController;

import java.time.Instant;

public class InfluxLogger {

    static String token = "3BNItTSkyHfZM-rP2qMbUI2HCWZuGD4lOa3XELzWYWxcgJtDFyV2zf2h3sf-BurgP4WKLJytfPGXAc04q9kfdg==";
    static String bucket = "dae2";
    static String org = "DaNussi";
    static InfluxDBClient client;
    static WriteApiBlocking writeApi;
    static Thread thread;

    public static void Init() {
        thread = new Thread(() -> {
            client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());
            writeApi = client.getWriteApiBlocking();
        });

        thread.start();
    }

    public static void Reset() {
        if(client != null) {
            client.close();
            client = null;
        }

        if(thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public static void OnVirtualInventory(AEKey item, String redisIndex, long offsetAmount, long currentAmount, String senderUUID) {
        if(!DedicatedAppliedEnergisticsController.IsRunning) return;


        Point point = Point
                .measurement(redisIndex)
                .addTag("item", item.getDisplayName().getString())
                .addTag("sender", senderUUID)
                .addField("offset_amount", offsetAmount)
                .addField("current_amount", currentAmount)
                .time(Instant.now(), WritePrecision.MS);

        writeApi.writePoint(bucket, org, point);

    }

}
