package module4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Module4 extends Application implements Runnable {

	List<File> list;
	File file;
	Map<String, Integer> output = new TreeMap<String, Integer>();

	public Module4() {

	}

	public Module4(File file) {
		this.file = file;
	}

	ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	public void start(final Stage stage) {

		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Add File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));

		Button addFileButton = new Button("Add File(s)");
		Button countWordsButton = new Button("Count Words");

		HBox hbox = new HBox(addFileButton, countWordsButton);
		hbox.setSpacing(5); // add space between buttons
		hbox.setPadding(new Insets(5)); // add space in front of button

		Scene scene = new Scene(hbox, 640, 480);
		stage.setScene(scene);
		stage.setTitle("Count Words in File");
		stage.show();

		addFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				list = fileChooser.showOpenMultipleDialog(stage);
				if (list != null) {

					Alert alert = new Alert(AlertType.INFORMATION);
					String s = "Your file(s) have been added.\n";

					for (File file : list) {
						s += file.getName() + "\n";
					}
					alert.setContentText(s);
					alert.show();
				}
			}
		});

		countWordsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(final ActionEvent e) {

				// run multithreaded process
				long begin = System.currentTimeMillis();
				run();
				executorService.shutdown();
				long end = System.currentTimeMillis();
				System.out.println("Multithreading total time: " + (end - begin));

				// run process with no threading
				try {
					long beginNonThreading = System.currentTimeMillis();
					countWordsInFileNoThreading(list);
					long endNonThreading = System.currentTimeMillis();
					System.out.println("No threading total time: " + (endNonThreading - beginNonThreading));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// display final message with file word count
				displayAlert();
			}
		});
	}

	public static void main(String[] args) {

		launch();
	}

	/*
	 * method to count words using a new thread for each file
	 */
	private void countWordsInFile(File file) throws FileNotFoundException {

		Scanner sc = new Scanner(new FileInputStream(file));

//			Thread t = new Thread();
//			t.start();

		int count = 0;
		try {
			while (sc.hasNext()) {
				sc.next();
				count++;
			}
			output.put(file.getName(), count);
		} finally {
			System.out.println(
					file.getName() + " words: " + count + " -- finished processing at : " + System.currentTimeMillis());
		}

	}

	/*
	 * Show the final output of the files that were processed with their word count
	 */
	private String displayMapAsString(Map<String, Integer> map) {
		String s = "";

		for (Entry<String, Integer> entry : map.entrySet()) {
			s += entry.getKey() + " " + entry.getValue().toString() + "\n";
		}
		return s;
	}

	private void displayAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		String s = displayMapAsString(output);
		alert.setTitle("Words per File");
		alert.setHeaderText("Words have been counted successfully");
		alert.setContentText(s);
		alert.show();
	}

	/*
	 * Used for multithreading
	 */
	@Override
	public void run() {

		TaskRunner task = new TaskRunner();

		// Create a new thread for each file in the list
		ExecutorService executorService = Executors.newFixedThreadPool(list.size());

		for (File file : list) {
			// Runnable worker = new Module4(file);

			try {
				// Thread t = new Thread();
				// t.start();
				// executorService.execute(t);

				countWordsInFile(file);

				executorService.execute(task);
				System.out.println(executorService.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Used to compare the amount of time it takes to count words in file with
	 * method using threads and method without threads
	 */
	private void countWordsInFileNoThreading(List<File> list) throws FileNotFoundException {
		Map<String, Integer> output = new TreeMap<String, Integer>();

		for (File file : list) {
			Scanner sc = new Scanner(new FileInputStream(file));

			try {
				int count = 0;
				while (sc.hasNext()) {
					sc.next();
					count++;
				}
				output.put(file.getName(), count);
			} finally {

			}
		}

	}

}