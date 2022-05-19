/*
 * Copyright 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ramidore.component;

import lombok.Getter;
import lombok.Setter;
import org.ramidore.core.IConfigurable;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;

/**
 * お知らせ領域 .
 *
 * @author atmark
 */
public class OshiraseJDialog extends JDialog implements IConfigurable {

    /**
     * マウスイベント.
     */
    private MouseEvent start;

    /**
     * ラベル.
     */
    @Getter
    @Setter
    private JLabel label;

    /**
     * コンストラクタ.
     *
     * @param config 設定
     */
    public OshiraseJDialog(Properties config) {

        // getContentPane()に設定しないと適用されない？
        getContentPane().setBackground(new Color(0x11, 0x11, 0x11, 0xFF));

        // ツールバー非表示
        setUndecorated(true);
        // 常に最前面
        setAlwaysOnTop(true);
        // setUndecoratedコール後じゃないとエラー

        new ComponentResizer(this);

        addMouseMotionListener(new WindowDrugMove());
        addMouseListener(new WindowDrugMove());

        loadConfig(config);
    }

    /**
     * ラベルを追加する.
     */
    public void addLabel() {
        // swingのラベル
        label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.CENTER);
        this.add(label);
    }

    /**
     * イベントリスナー.
     *
     * @author atmark
     */
    private class WindowDrugMove implements MouseInputListener, MouseListener {

        /**
         * 座標.
         */
        private Point loc;

        @Override
        public void mouseMoved(MouseEvent me) {
        }

        @Override
        public void mouseDragged(MouseEvent me) {

            Window window = OshiraseJDialog.this;

            loc = window.getLocation(loc);

            int x = loc.x - start.getX() + me.getX();

            int y = loc.y - start.getY() + me.getY();

            window.setLocation(x, y);
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
            // 最初に掴んだポイントを記憶
            start = me;
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }
    }

    @Override
    public void loadConfig(Properties config) {

        int x = Integer.parseInt(config.getProperty("oshirase.xPos", "0"));

        int y = Integer.parseInt(config.getProperty("oshirase.yPos", "0"));

        this.setLocation(x, y);

        int w = Integer.parseInt(config.getProperty("oshirase.width", "400"));

        int h = Integer.parseInt(config.getProperty("oshirase.height", "30"));

        this.setSize(w, h);

        float opacity = Float.parseFloat(config.getProperty("oshirase.opacity", "0.75f"));

        this.setOpacity(opacity);
    }

    @Override
    public void saveConfig(Properties config) {

        Point p = this.getLocation();

        config.setProperty("oshirase.xPos", String.valueOf(p.x));
        config.setProperty("oshirase.yPos", String.valueOf(p.y));

        Dimension d = this.getSize();

        config.setProperty("oshirase.width", String.valueOf(d.width));
        config.setProperty("oshirase.height", String.valueOf(d.height));

        float opacity = this.getOpacity();

        config.setProperty("oshirase.opacity", String.valueOf(opacity));
    }

    @Override
    public final void loadConfig() {
        // nop
    }

    @Override
    public final void saveConfig() {
        // nop
    }
}
