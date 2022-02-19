package net.Duels.datastorage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.utility.Pair;

public class MySQL {
	
	@Getter
	private Connection connection;

	public MySQL() {
		try {
			this.connect();
			this.initTables();
		} catch (Exception e) {
			e.printStackTrace();
			Duel.getMainConfig().setDataType(DataStorage.DataType.FILE);
			Duel.log(Duel.LOG_LEVEL.ERROR, "The MYSQL data specified in the config is invalid!");
		}
	}

	public void executeQuery(String query, Object... data) {
		PreparedStatement preparedStatement = null;
		try {
			this.connect();
			preparedStatement = this.connection.prepareStatement(query);
			for (int i = 0; i < data.length; ++i) {
				preparedStatement.setObject(i + 1, data[i]);
			}
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public boolean containsData(UUID key) {
		int count = 0;
		PreparedStatement preparedStatement = null;
		try {
			this.connect();
			String query = "SELECT Count(`player_id`) FROM `players` WHERE `uuid` = ? LIMIT 1;";
			preparedStatement = this.connection.prepareStatement(query);
			preparedStatement.setString(1, key.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				count = resultSet.getInt(1);
			}
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return count > 0;
	}

	public Pair<ResultSet, PreparedStatement> getResult(UUID uuid) {
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		try {
			this.connect();
			String query = "SELECT * FROM `players` WHERE `uuid` = ?;";
			preparedStatement = this.connection.prepareStatement(query);
			preparedStatement.setString(1, uuid.toString());
			result = preparedStatement.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return new Pair<>(result, preparedStatement);
	}

	public Pair<ResultSet, PreparedStatement> getResult() {
		ResultSet result = null;
		PreparedStatement preparedStatement = null;
		try {
			this.connect();
			String query = "SELECT * FROM `players`;";
			preparedStatement = this.connection.prepareStatement(query);
			result = preparedStatement.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					return new Pair<>(result, preparedStatement);
				}
				return new Pair<>(result, preparedStatement);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return new Pair<>(result, preparedStatement);
	}

	private void initTables() {
		try {
			this.connect();
			InputStream inputStream = Duel.getInstance().getResource("MySQL.sql");
			int size = inputStream.available();
			byte[] buffer = new byte[size];
			int read = inputStream.read(buffer);
			if (read <= 0) {
				throw new IOException("The file could not be read!");
			}
			String[] structure = new String(buffer).split(";");
			if (structure.length == 0) {
				throw new RuntimeException("Table Read Error!");
			}
			this.connection.setAutoCommit(false);
			Statement statement = this.connection.createStatement();
			String[] array;
			for (int length = (array = structure).length, i = 0; i < length; ++i) {
				String query = array[i];
				query = query.trim();
				if (!query.isEmpty()) {
					statement.execute(query);
				}
			}
			this.connection.commit();
			this.connection.setAutoCommit(true);
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public void connect() throws SQLException {
		synchronized (this) {
			if (this.connection != null && !this.connection.isClosed()) {
				return;
			}
			String host = (String) Duel.getMainConfig().getMapping().getOrDefault("mysql.host", "localhost");
			int port = (int) Duel.getMainConfig().getMapping().getOrDefault("mysql.port", "3306");
			String user = (String) Duel.getMainConfig().getMapping().getOrDefault("mysql.user", "duel");
			String password = (String) Duel.getMainConfig().getMapping().getOrDefault("mysql.password", "duel");
			String database = (String) Duel.getMainConfig().getMapping().getOrDefault("mysql.database", "duel");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user,
					password);
			Duel.log(Duel.LOG_LEVEL.INFO, "MySQL connected successfully");
		}
	}

	public void disconnect() throws SQLException {
		if (this.connection == null || !this.connection.isClosed()) {
			return;
		}
		this.connection.close();
	}
	
}
