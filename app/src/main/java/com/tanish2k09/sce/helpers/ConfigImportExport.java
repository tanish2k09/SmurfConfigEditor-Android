package com.tanish2k09.sce.helpers;

import com.tanish2k09.sce.R;
import com.tanish2k09.sce.utils.ConfigCacheClass;
import com.tanish2k09.sce.utils.StringValClass;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.io.SuFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigImportExport {
    private File configFile;
    private Context ctx;
    private int pasteDump = 0;

    public ConfigImportExport(Context context) {
        ctx = context;
    }

    private int openConfig() {
        configFile = new File(Environment.getExternalStorageDirectory().getPath() + "/SmurfKernel",
                            ctx.getResources().getString(R.string.configFile));
        if (configFile.exists()) {
            return 0;
        } else {
            return -1;
        }
    }

    private boolean isValidConfigLine(String line) {
        return (line.split("=").length == 2);
    }

    public boolean configImport() {
        if (openConfig() != 0) {
            if (configDumpRoot()) {
                Toast.makeText(ctx, ctx.getString(R.string.importRoot), Toast.LENGTH_SHORT).show();
                return configImport();
            } else if (configDumpInflate()) {
                Toast.makeText(ctx, ctx.getString(R.string.importInflate), Toast.LENGTH_SHORT).show();
                return configImport();
            }
            return false;
        } else {
            try {
                ConfigCacheClass.clearAll();
                BufferedReader inBR = new BufferedReader(new FileReader(configFile));
                String cache = inBR.readLine();

                while (cache != null) {
                    if (isValidConfigLine(cache)) {
                        String[] configPair;
                        if (cache.startsWith("#")) {
                            cache = cache.substring(1);
                            configPair = cache.split("=");
                            ConfigCacheClass.addConfig(configPair[0], configPair[1], false);
                        } else {
                            configPair = cache.split("=");
                            ConfigCacheClass.addConfig(configPair[0], configPair[1], true);
                        }
                    }
                    cache = inBR.readLine();
                }
                return true;
            } catch (IOException e) {
                Toast.makeText(ctx, R.string.swwRC, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }
    }

    public void saveConfig() {

        try {
            Toast.makeText(ctx, "new? " + configFile.createNewFile(), Toast.LENGTH_SHORT).show();

            BufferedWriter outBW = new BufferedWriter(new FileWriter(configFile));
            outBW.write(ctx.getString(R.string.configStamp));


            for (int idx = 0; idx < ConfigCacheClass.getConfiglistSize(); ++idx) {
                StringValClass svc = ConfigCacheClass.getStringVal(idx);

                assert svc != null;
                for (int option = 0; option < svc.getNumOptions(); ++option) {
                    StringBuilder lineToWrite = new StringBuilder();
                    if (!svc.getOption(option).equals(svc.getActiveVal()) &&
                        !svc.getName().equals(ctx.getString(R.string.profileVersion)))
                            lineToWrite.append("#");

                    lineToWrite.append(svc.getName()).append("=").append(svc.getOption(option));
                    outBW.write(lineToWrite.toString());
                    outBW.newLine();
                }
                outBW.newLine();
                outBW.newLine();
            }
            outBW.flush();
            outBW.close();
            Toast.makeText(ctx, "Saved successfully", Toast.LENGTH_SHORT).show();
            Shell.su("sh /init.smurf.sh").submit();
        } catch (IOException e) {
            Toast.makeText(ctx, R.string.swwRC, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void configDump() {
        if (pasteDump == 1)
            configDumpRoot();
        else if (pasteDump == 2)
            configDumpInflate();
    }

    private boolean configDumpRoot() {
        Toast.makeText(ctx, configFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        String filename = ctx.getResources().getString(R.string.configFile);
        File rootConfig = SuFile.open("/"+ filename);
        if (rootConfig.exists())
            Shell.su("cp /" + filename + " " + configFile.getAbsolutePath()).exec();
        return configFile.exists();
    }

    private boolean configDumpInflate() {
        return false;
    }
}
