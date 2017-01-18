package felixserrano.example.org.videoplayer;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity  implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,SurfaceHolder.Callback,View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private EditText editText;
    private ImageButton bPlay, bPause, bStop, bLog;
    private TextView logTextView;
    private boolean pause, stop;
    private String path;
    private int savePos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        editText = (EditText)findViewById(R.id.path);
        editText.setText("http://campus.somtic.net/android/video1617.mp4");
        logTextView = (TextView)findViewById(R.id.Log);
        bPlay = (ImageButton) findViewById(R.id.play);
        bPlay.setOnClickListener(this);
        bPause = (ImageButton) findViewById(R.id.pause);
        bPause.setOnClickListener(this);
        bStop = (ImageButton) findViewById(R.id.stop);
        bStop.setOnClickListener(this);
        bLog = (ImageButton) findViewById(R.id.logButton);
        bLog.setOnClickListener(this);
        log("");
    }

    private  void log(String s)
    {
        logTextView.append(s+ "\n");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.play:
                if(mediaPlayer != null) {
                    if (pause)
                        mediaPlayer.start();
                    else
                        playVideo();
                }
                break;
            case R.id.pause:
                if(mediaPlayer != null) {
                    pause = true;
                    mediaPlayer.pause();
                }
                break;
            case R.id.stop:
                if(mediaPlayer != null) {
                    pause = false;
                    mediaPlayer.stop();
                    savePos = 0;
                    stop = true;
                }
                break;
            case R.id.logButton:
                logTextView.setVisibility(logTextView.getVisibility() == TextView.VISIBLE ? TextView.INVISIBLE : TextView.VISIBLE);
                break;
        }

    }

    private  void playVideo() {
        try {
            pause = false;
            path = editText.getText().toString();
            if(mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            if (stop)
                mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceHolder);
            //mediaPlayer.prepare();
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.seekTo(savePos);
            stop = false;
        }catch (Exception e) {
            log("ERROR:" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!= null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null && !pause)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer != null && !pause)
            mediaPlayer.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mediaPlayer != null){
            int pos = mediaPlayer.getCurrentPosition();
            outState.putString("ruta",path);
            outState.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null)
        {
            path = savedInstanceState.getString("ruta");
            savePos = savedInstanceState.getInt("posicion");
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        log("onBufferingUpdate porcentaje" + i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        log("llamada a onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        log("llamada a onPrepared");
        int mVideoWidth = mediaPlayer.getVideoWidth();
        int mVideoHeight = mediaPlayer.getVideoHeight();
        if(mVideoWidth != 0 && mVideoHeight != 0)
        {
            surfaceHolder.setFixedSize(mVideoWidth,mVideoHeight);
            mediaPlayer.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        log("llamada a surfaceCreated");
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        log("llamada a surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        log("llamada a surfaceFestroyed");
    }
}
