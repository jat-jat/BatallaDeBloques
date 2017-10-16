package proyectojuego;

import ibxm.Channel;
import ibxm.IBXM;
import ibxm.Module;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Reproductor {
    private static final int SAMPLE_RATE = 48000;
    
    private Module modulo;
    private IBXM ibxmPlayer;
    private Thread hiloDeReproduccion;
    private volatile boolean estaReproduciendo;
    
    public Reproductor() {
        ibxmPlayer = null;
        modulo = null;
        estaReproduciendo = false;
    }
    
    public void cargarArchivo( InputStream archivoMod ) throws Exception {
        /*String nombreDelArchivo = archivoMod.getName();
        if(nombreDelArchivo.lastIndexOf(".") > 0){
            String extension = nombreDelArchivo.substring(nombreDelArchivo.lastIndexOf(".") + 1);
            if(!(extension.equalsIgnoreCase("mod") || extension.equalsIgnoreCase("ft") || extension.equalsIgnoreCase("s3m") || extension.equalsIgnoreCase("xm")))
                throw new Exception("El archivo no tiene una extensión válida.");
        }
        else
            throw new Exception("El archivo no tiene extensión.");*/
        
        try {
            Module module = new Module( archivoMod );
            IBXM ibxm = new IBXM( module, SAMPLE_RATE );
            ibxm.setInterpolation( Channel.LINEAR );
            synchronized( this ) {
                modulo = module;
                ibxmPlayer = ibxm;
            }
        } finally {
            archivoMod.close();
        }
    }
    
    public synchronized void reproducir() {
        if (ibxmPlayer != null) {
            estaReproduciendo = true;
            hiloDeReproduccion = new Thread(new Runnable() {
                public void run() {
                    int[] mixBuf = new int[ibxmPlayer.getMixBufferLength()];
                    byte[] outBuf = new byte[mixBuf.length * 2];
                    AudioFormat audioFormat = null;
                    SourceDataLine audioLine = null;
                    try {
                        audioFormat = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
                        audioLine = AudioSystem.getSourceDataLine(audioFormat);
                        audioLine.open();
                        audioLine.start();
                        while (estaReproduciendo) {
                            int count = getAudio(mixBuf);
                            int outIdx = 0;
                            for (int mixIdx = 0, mixEnd = count * 2; mixIdx < mixEnd; mixIdx++) {
                                int ampl = mixBuf[mixIdx];
                                if (ampl > 32767) {
                                    ampl = 32767;
                                }
                                if (ampl < -32768) {
                                    ampl = -32768;
                                }
                                outBuf[outIdx++] = (byte) (ampl >> 8);
                                outBuf[outIdx++] = (byte) ampl;
                            }
                            audioLine.write(outBuf, 0, outIdx);
                        }
                        audioLine.drain();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    } finally {
                        if (audioLine != null && audioLine.isOpen()) {
                            audioLine.close();
                        }
                    }
                }
            });
            hiloDeReproduccion.start();
        }
    }
    
    public synchronized void detener() {
        estaReproduciendo = false;
        try {
            if( hiloDeReproduccion != null ) hiloDeReproduccion.join();
        } catch( InterruptedException e ) {}
    }
    
    public synchronized void deshabilitarCanal(int numCanal){
        if(ibxmPlayer != null)
            ibxmPlayer.setMuted(numCanal, true);
    }
    
    public synchronized void habilitarCanal(int numCanal){
        if(ibxmPlayer != null)
            ibxmPlayer.setMuted(numCanal, false);
    }
    
    public synchronized int getNumCanales(){
        if(modulo != null)
            return modulo.numChannels;
        else
            return 0;
    }
    
    private synchronized int getAudio(int[] mixBuf) {
        int count = ibxmPlayer.getAudio(mixBuf);
        return count;
    }
    
    public boolean estaReproduciendo(){
        return estaReproduciendo;
    }
}