package main.epicmediaplayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView<String> songList;

    @FXML
    private Button playButton, nextButton, backButton;

    @FXML
    private Label songView, videoView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Slider volumeSlider;

    private List<Media> mediaList;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private Timeline timeline;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        mediaList = new ArrayList<>(); // An array list, which stores music files and video files

        songView.setOnMouseClicked(event -> { // When user clicks on "SONGS":
            stopMedia(); // Stop playing music, if there was music playing
            loadMusicFiles(); // Load the music files, defined later on
            songView.setDisable(true); // This is for the UI to display, a javaFX action
            videoView.setDisable(false); // This is for the UI to NOT display, also a javaFX action
        });

        videoView.setOnMouseClicked(event -> { // When the user clicks on "VIDEOS"
            stopMedia(); // Stop playing music, if there was music playing
            loadVideoFiles();// Load the video files, defined later on too
            videoView.setDisable(true); // For UI to display, again
            songView.setDisable(false); // For UI to NOT display, again
        });

        playButton.setOnAction(event -> { // When action is performed on the "play" button:
            if (!isPlaying) { // if boolean of isPlaying is false:

                if (isPaused) { // if boolean of isPaused is true:

                    resumeMedia(); // perform the function to resume the paused music, function defined later on

                } else { // if it was anything but isPaused:

                    playMedia(); // perform function to start playing music, function defined later on

                }

            } else { // if isPlaying was true:

                pauseMedia(); // perform pause function, also defined later on

            }

        });

        songList.setOnMouseClicked(event -> { // if an item from the song list was clicked:

            if (event.getClickCount() == 2) { // if it was clicked twice:

                int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get the clicked item index and store to variable "selectedIndex"

                if (selectedIndex >= 0) { // if the selected index was in the valid range:

                    Media selectedMedia = mediaList.get(selectedIndex); // javaFX way of getting the selected item as a media
                    openVideoInNewWindow(selectedMedia); // function to open the selected item in new window, it was meant for videos tho but still works either way

                }
            }

        });
    }

    private void loadMusicFiles() { // A function to load music files

        File musicFolder = new File("music"); // store the path/directory of the music inside a "File" object variable called "musicFolder"
        File[] musicFiles = musicFolder.listFiles(); // Get the files from musicFolder and store them as an Array inside another "File" object variable called "musicFiles"

        if (musicFiles != null) { // if musicFiles isn't empty:

            mediaList.clear(); // empty up the mediaList
            songList.getItems().clear(); // empty up the songList

            for (File file : musicFiles) { // for every single "File" object inside "musicFiles"

                if (file.isFile() && file.getName().endsWith(".wav")) { // if the file is valid and has the format of ".wav":

                    mediaList.add(new Music(file.getAbsolutePath())); // add the mediaList with this file
                    songList.getItems().add(file.getName()); // add the name of the file to songList, to be displayed in UI

                }

            }

        }

    }

    private void loadVideoFiles() { // Now the same thing but for videos now

        File videoFolder = new File("video"); // store the directory in videoFolder
        File[] videoFiles = videoFolder.listFiles(); // take the files from that directory, and store it in an array list

        if (videoFiles != null) { // if the Files ain't empty:

            mediaList.clear(); // clear up the mediaList
            songList.getItems().clear(); // clear up songList

            for (File file : videoFiles) { // for every file inside videFiles:

                if (file.isFile() && file.getName().endsWith(".mp4")) { // if they valid and got that mp4 format:

                    mediaList.add(new Video(file.getAbsolutePath())); // add the file to mediaList
                    songList.getItems().add(file.getName()); // add the name of the file to songList, which is just the UI (ik, weird choice of name, songList is actually just the UI displaying names of files click)

                }

            }

        }

    }

    @FXML
    private void playMedia() { // function to play


        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get the selected item from the UI (songList), store inside variable

        if (selectedIndex < 0) { // if selected index was somehow managed to be invalid:

            return; // do nothing. Just a counter measure, does not hurt to implement right?

        }

        Media selectedMedia = mediaList.get(selectedIndex); // Make "Media" object variable "selectedMedia" store the selected song

        try { // perform:

            selectedMedia.play(); // play() is a function to play music, defined later

            isPlaying = true; // set isPlaying boolean to true
            isPaused = false; // set isPaused boolean to false

            playButton.setText("Pause"); // Change UI button text to "Pause"

            if (selectedMedia instanceof Music) { // if selectedMedia is an instance of a Music class:

                updateProgressBar((Music) selectedMedia); // perform the function for progress bar updates, defined later

            }

        }

        catch (Exception e) { // In case something went wrong:

            e.printStackTrace(); // print that error

        }

    }

    @FXML
    private void pauseMedia() { // Function to pause

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get the number of the song

        if (selectedIndex < 0) { // if somehow number is invalid:

            return; // do nothing

        }

        Media selectedMedia = mediaList.get(selectedIndex); // Get the specified song inside the variable

        selectedMedia.pause(); // now taking that variable, perform the pause function (defined later)

        isPlaying = false; // adjust boolean isPlaying to false
        isPaused = true; // adjust boolean isPaused to true

        playButton.setText("Play"); // change the text of the Button

        stopProgressBarUpdate(); // function to stop updating the progress bar, defined later
    }

    @FXML
    private void resumeMedia() { // function to continue music from paused state

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // again, just get the number of the song

        if (selectedIndex < 0) { // if somehow number was not valid
            return; // do nothing
        }

        Media selectedMedia = mediaList.get(selectedIndex); // Get the song inside variable, like before

        selectedMedia.resume(); // now take that variable and perform the resume function on it (defined later)

        isPlaying = true; // adjust boolean
        isPaused = false; // adjust boolean

        playButton.setText("Pause"); // change button text

        updateProgressBar((Music) selectedMedia); // function to update the progress bar (defined later)
    }

    @FXML
    private void nextMedia() { // function to play the next song

        stopMedia(); // Stop the currently playing item

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get the selected item's order number

        int nextIndex = selectedIndex + 1; // now take the number after the selected item and store to a new int variable

        if (nextIndex >= mediaList.size()) { // If  next item after the selected item does not exist:

            nextIndex = 0; // Store the first item in the list instead

        }

        songList.getSelectionModel().select(nextIndex); // select the item which comes after currently selected

        playMedia(); // And then play that next item! or well, that function will do it for us

    }

    @FXML
    private void previousMedia() { // play the previous media, same thing as next media but just -1 instead of +1

        stopMedia(); // stop playing

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get item number

        int previousIndex = selectedIndex - 1; // store what comes before that item number

        if (previousIndex < 0) { // If there is no item before the selected item

            previousIndex = mediaList.size() - 1; //  // Play the last item in the list!

        }

        songList.getSelectionModel().select(previousIndex); // select the item which comes before currently selected

        playMedia(); // And then play it

    }
    @FXML
    private void stopMedia() { // function to completely stop playing

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get selected item number

        if (selectedIndex >= 0) { // if the item number is valid:

            Media selectedMedia = mediaList.get(selectedIndex); // put the song in Media object variable selectedMedia
            selectedMedia.stop(); // and then perform the stop function on it, defined later

        }

    }


    private void updateProgressBar(Music music) { // function to update progress bar

        stopProgressBarUpdate(); // Stop previous updates from the progress bar, function defined later

        long duration = music.getClip().getMicrosecondLength(); // Create variable "duration" storing time of playing music in microseconds



        timeline = new Timeline( // javaFX object for the progressbar, stored in variable "timeline"

                new KeyFrame(Duration.ZERO, event -> { // create a timeline with a KeyFrame that updates the progress bar, another javaFX thing

                    long position = music.getClip().getMicrosecondPosition(); // get the current position of the music in microseconds

                    double progress = (double) position / duration; // calculate the progress as a ratio of the current position to the duration

                    progressBar.setProgress(progress); // update the progress bar with the calculated progress

                }),

                new KeyFrame(Duration.seconds(1)) // Set the KeyFrame to update every second

        );

        timeline.setCycleCount(Timeline.INDEFINITE); // Set the timeline to keep on repeating indefinitely

        timeline.play(); // Start the timeline to begin updating the progress bar

    }


    private void stopProgressBarUpdate() { // to stop the progress bar

        if (timeline != null) { // if the progressbar isn't empty:

            timeline.stop(); // stop the timeline
            timeline = null; // empty up the timeline

        }

    }

    private void openVideoInNewWindow(Media media) { // for opening items in the local OS media player instead

        File videoFile = new File(media.getFilePath()); // get the file

        if (Desktop.isDesktopSupported()) { // if desktop access is supported:

            Desktop desktop = Desktop.getDesktop(); // define the action to access desktop

            if (desktop.isSupported(Desktop.Action.OPEN)) { // if actions is supported for desktop:

                try {
                    desktop.open(videoFile); // open the video through desktop, works for songs too
                }

                catch (IOException e) { // just in case something goes wrong, print the error
                    e.printStackTrace();
                }

            }
        }
    }
    private void adjustVolume() { // for adjusting volume

        int selectedIndex = songList.getSelectionModel().getSelectedIndex(); // get the selected song's number

        if (selectedIndex < 0) { // if selected song's number isn't valid:
            return; // do nothing
        }

        Media selectedMedia = mediaList.get(selectedIndex); // get the song

        if (selectedMedia instanceof Music) { // if selected song is an instace of Music:

            Music music = (Music) selectedMedia; // get the music
            Clip clip = music.getClip(); // access the music
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // trippy command line, to utilize volume of music to the slider
            double minVolume = volumeControl.getMinimum(); // store the minimum  volume
            double maxVolume = volumeControl.getMaximum(); // store the maximum volume

            double volume = volumeSlider.getValue(); // read the value of the slider
            if (volume < 0.0 || volume > 1.0) { // a safety measure, if slider somehow goes beyond 1 or lower than 0, do nothing (better than getting an error and crashing..)
                return;
            }

            double adjustedVolume = minVolume + (maxVolume - minVolume) * volume; // calculation for volume value
            volumeControl.setValue((float) adjustedVolume); // now adjust the volume by that calculated value. This method is slow and probably inefficient, but it works!
        }
    }


    @FXML
    private void handleVolumeDragDetected(MouseEvent event) { // A function to make the volume slider react to the adjusting of volume

        volumeSlider.setOnMouseDragged(e -> adjustVolume()); // when there is drag on volume slider, perform adjustVolume function

    }


    interface Media { // Define the Media interface
        String getFilePath();
        void play() throws Exception;
        void pause();
        void resume();
        void stop();
    }


    class Music implements Media { // Music class
        private Clip clip; // for utilizing sound
        private long clipTime; // for utilizing sound time
        private String filePath; // for reading directory to music

        public Music(String filePath) {

            this.filePath = filePath;  // get the file (which definitely has the .wav format)

            try {

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));// retrieve an audio input  from the file path
                AudioFormat audioFormat = audioInputStream.getFormat(); // get the audio format from the audio input stream
                DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);// create an instance "DataLine.Info" to specify the type of clip and the audio format

                clip = (Clip) AudioSystem.getLine(info); // Get a clip from the audio system based on the provided info
                clip.open(audioInputStream);// Open the clip with the audio input stream



            }

            catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) { // all these errors are what I had face, so just if they happen again, print it
                e.printStackTrace();
            }
        }

        @Override
        public String getFilePath() { // not many modifications done here

            return filePath; // '_'
        }

        @Override
        public void play() throws Exception { // a little modification to da play function

            if (clip != null) { // if clip is not empty

                if (isPaused) { // if boolean isPaused = true

                    clip.setMicrosecondPosition(0); // Reset the position to the start

                }

                else {

                    clip.stop(); // stop the music
                    clip.setMicrosecondPosition(0); // reset the position to the start

                }

                clip.start(); // Start playing the music

            }
        }

        @Override
        public void pause() { // a little modification to da pause function

            if (clip != null && clip.isActive()) { // if clip isn't empty and is playing:

                clipTime = clip.getMicrosecondPosition(); // get the time of clip in microseconds
                clip.stop(); // stop the playback of the clip
                isPlaying = false; // adjust boolean
                isPaused = true; // adjust boolean
                playButton.setText("Play"); // adjust button text

            }

        }

        public void stop() {

            if (clip != null) { // if clip is not empty:

                clip.stop();  // stop the playback
                isPlaying = false; // adjust boolean
                isPaused = false; // adjust boolean
                playButton.setText("Play"); // adjust text

                stopProgressBarUpdate(); // update the progress bar

            }

        }

        @Override
        public void resume() { // modification for resume function

            if (clip != null && isPaused) { // if the clip is not empty and is paused:
                clip.start(); // start the playback
                isPlaying = true; // set boolean
                isPaused = false; // set boolean
                playButton.setText("Pause"); // set button text

            }

        }

        public Clip getClip() { // not many modifications done here

            return clip; // '_'

        }
    }


    class Video implements Media { // Video class!
        private String filePath;

        public Video(String filePath) {

            this.filePath = filePath; // initialize video's own file path

        }

        @Override
        public String getFilePath() {

            return filePath; // '_'

        }

        @Override
        public void play() throws Exception { // modifications to the play function

            File videoFile = new File(filePath); // get the file

            if (Desktop.isDesktopSupported()) { // if desktop access is supported:

                Desktop desktop = Desktop.getDesktop(); // store method to access the desktop

                if (desktop.isSupported(Desktop.Action.OPEN)) { // if desktop actions is supported:

                    desktop.open(videoFile); // open the file in the desktop's local media player

                }
            }
        }

        @Override
        public void pause() {
            // Nothing here
        }

        @Override
        public void resume() {
            // Nothing here
        }

        @Override
        public void stop() {
            // Nothing here
        }
    }
}
