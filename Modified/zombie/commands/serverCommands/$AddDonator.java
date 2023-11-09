package zombie.commands.serverCommands;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;

import zombie.ZomboidFileSystem;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.raknet.UdpConnection;
import zombie.core.secure.PZcrypt;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.util.PZSQLUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

@CommandName(
        name = "adddonator"
)

@CommandHelp(
        helpText = "Кост, не забудь исправить"
)

@RequiredRight(
        requiredRights = 32
)

public class $AddDonator extends CommandBase {
    public $AddDonator(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }

    protected String Command() {
        String result = "Successful Done!";
        //adddonator Cost_Your #Cost_Your 500
        if (this.getCommandArgsCount() == 3) {
            String playerName = this.getCommandArg(0);
            String playerDCName = this.getCommandArg(1); // В Args
            int sum = Integer.parseInt(this.getCommandArg(2)); // В Args
            int donateLevel = 0;  //TODO: Может можно сделать кейс

            if (sum > 100 & sum < 200) {
                donateLevel = 1;
            } else if (sum >= 200 & sum < 300) {
                donateLevel = 2;
            } else if (sum >= 300 & sum < 400) {
                donateLevel = 3;
            } else if (sum >= 400 & sum < 500) {
                donateLevel = 4;
            } else if (sum >= 500) {
                donateLevel = 5;
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String securityCode = "" ;
            //TODO: Вставить все это в запрос к БД
            //TODO:код генерации ключа - автоматом

        /* -
         -  MySQL -
         - Определяем путь до кеша игры
         - Определяем путь до БД
         - Получаем соединение с базой данных
         - Создаем шаблон запроса к БД: если таблицы с донатерами нет, то создаем ее
         - Создаем шаблон запроса к БД: добавляем донатера в таблицу
         - Организуем и выполняем запрос к БД
         - Закрываем запрос
         - Закрываем соединение
         - */


            String dirCash = ZomboidFileSystem.instance.getCacheDir();
            File dbFile = new File(dirCash + File.separator + "db" + File.separator + GameServer.ServerName + ".db");
            try {
                Connection conn = PZSQLUtils.getConnection(dbFile.getAbsolutePath());
                Statement state = conn.createStatement();
                state.executeUpdate("CREATE TABLE IF NOT EXISTS donators (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, playerName STRING, playerNameDC STRING, donateLevel INTEGER, donateSumm INTEGER, gettedTime TIMESTAMP, securityCode STRING);");
                //TODO: Тут ниже лучше юзнуть String.format()..
                state.executeQuery(String.format("insert into donators (playerName, playerNameDC, donateLevel, donateSumm, gettedTime, securityCode) values ('%s', '%s', %d, %d, %s, '%s');", playerName, playerDCName, donateLevel, sum, timestamp.toString(), securityCode));
                // Актуальный запрос на добавление игрока "insert into donators values (12, 'name', 'DC', 12, 500, NULL, 'dwdwd')"
                state.close();
                conn.close();
            } catch (SQLException e) {
                result = "Some error: " + e.toString();
                throw new RuntimeException(e);
            }
        }
        else {
            result = "Что-то пошло не так";
        }


        return result;
    }
}