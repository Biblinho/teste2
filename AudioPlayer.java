/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jogorpg;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private Clip musicaClip;  // música de fundo
    private Clip efeitoClip;  // efeitos curtos

    // Música de fundo em loop
    public void playMusica(String caminho) {
        stopMusica();
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(caminho));
            musicaClip = AudioSystem.getClip();
            musicaClip.open(audioStream);
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Erro ao tocar música: " + e.getMessage());
        }
    }

    public void stopMusica() {
        if (musicaClip != null && musicaClip.isRunning()) {
            musicaClip.stop();
            musicaClip.close();
        }
    }

    // Efeitos sonoros curtos
    public void playEfeito(String caminho, float volume) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(caminho));
                efeitoClip = AudioSystem.getClip();
                efeitoClip.open(audioStream);
                if (efeitoClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl fc = (FloatControl) efeitoClip.getControl(FloatControl.Type.MASTER_GAIN);
                    fc.setValue(volume); // ajustar volume do efeito
                }
                efeitoClip.start();
            } catch (Exception e) {
                System.out.println("Erro ao tocar efeito: " + e.getMessage());
            }
        }).start();
    }
}

 

