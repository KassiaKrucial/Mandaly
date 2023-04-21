package cz.czechitas.mandala;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import net.miginfocom.swing.*;
import net.sevecek.util.*;

public class HlavniOkno extends JFrame {

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    JMenuBar menuBar1;
    JLabel labOpen;
    JLabel labSaveAs;
    JMenuBar menuBar2;
    JLabel labShadesOfBlack;
    JLabel labShadesOfYellow;
    JLabel labShadesOfGold;
    JLabel labShadesOfOrange;
    JLabel labShadesOfRed;
    JLabel labShadesOfCrimson;
    JLabel labShadesOfDeepPink;
    JLabel labShadesOfPurple;
    JMenuBar menuBar3;
    JLabel labShadesOfDarkOrchid;
    JLabel labShadesOfBlue;
    JLabel labShadesOfDeepSkyBlue;
    JLabel labShadesOfAqua;
    JLabel labShadesOfSpringGreen;
    JLabel labShadesOfForestGreen;
    JLabel labShadesOfLime;
    JLabel labAktualniBarva;
    JLabel labBarva1;
    JLabel labBarva2;
    JLabel labBarva3;
    JLabel labBarva4;
    JLabel labBarva5;
    JScrollPane scrollPaneObrazku;
    JLabel labObrazek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    JPanel contentPane;
    MigLayout migLayoutManager;
    BufferedImage obrazek;
    Graphics2D stetec;
    JFileChooser dialog = new JFileChooser(".");

    public HlavniOkno() {
        initComponents();
        nahrajVychoziObrazek();

        stetec = (Graphics2D) obrazek.getGraphics();
        stetec.getColor();
        stetec.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        labObrazek.repaint();
    }

    private void nahrajVychoziObrazek() {
        try {
            InputStream zdrojObrazku = getClass().getResourceAsStream
                    ("/cz/czechitas/mandala/vychozi-mandala.png");
            obrazek = ImageIO.read(zdrojObrazku);
            labObrazek.setIcon(new ImageIcon(obrazek));
            labObrazek.setMinimumSize
                    (new Dimension(obrazek.getWidth(), obrazek.getHeight()));
        } catch (IOException ex) {
            throw new ApplicationPublicException(ex,
                    "Nepodařilo se nahrát obrázek mandaly:\n\n"
                            + ex.getMessage());
        }
    }

    private void priStisknutiLabBarva(MouseEvent e) {
        JLabel barva = (JLabel) e.getSource();
        stetec.setColor(barva.getBackground());
    }

    private void priStisknutiMysiNadLabObrazek(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        vyplnObrazek(obrazek, x, y, stetec.getColor());
        labObrazek.repaint();
    }

    private void priStisknutiLabOpen(MouseEvent e) {
        otevritObrazek();
    }

    private void priStisknutiLabSaveAs(MouseEvent e) {
        ulozitJako();
    }

    private void otevritObrazek() {
        int vysledek = dialog.showOpenDialog(this);
        if (vysledek != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File soubor = dialog.getSelectedFile();
        nahrajObrazekZeSouboru(soubor);

        // Zvetsi okno presne na obrazek
        pack();
        setMinimumSize(getSize());
    }

    private void nahrajObrazekZeSouboru(File soubor) {
        try {
            obrazek = ImageIO.read(soubor);
            labObrazek.setIcon(new ImageIcon(obrazek));
            labObrazek.setMinimumSize(new Dimension(obrazek.getWidth(), obrazek.getHeight()));
        } catch (IOException ex) {
            throw new ApplicationPublicException(ex, "Nepodařilo se nahrát obrázek mandaly ze souboru " + soubor.getAbsolutePath());
        }
    }

    private void ulozitJako() {
        int vysledek = dialog.showSaveDialog(this);
        if (vysledek != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File soubor = dialog.getSelectedFile();

        // Dopln pripadne chybejici priponu .png
        if (!soubor.getName().contains(".") && !soubor.exists()) {
            soubor = new File(soubor.getParentFile(), soubor.getName() + ".png");
        }

        // Opravdu prepsat existujici soubor?
        if (soubor.exists()) {
            int potvrzeni = JOptionPane.showConfirmDialog(this, "Soubor " + soubor.getName() + " už existuje.\nChcete jej přepsat?", "Přepsat soubor?", JOptionPane.YES_NO_OPTION);
            if (potvrzeni == JOptionPane.NO_OPTION) {
                return;
            }
        }

        ulozObrazekDoSouboru(soubor);
    }

    private void ulozObrazekDoSouboru(File soubor) {
        try {
            ImageIO.write(obrazek, "png", soubor);
        } catch (IOException ex) {
            throw new ApplicationPublicException(ex, "Nepodařilo se uložit obrázek mandaly do souboru " + soubor.getAbsolutePath());
        }
    }

    /**
     * Vyplni <code>BufferedImage obrazek</code>
     * na pozicich <code>int x</code>, <code>int y</code>
     * barvou <code>Color barva</code>
     */
    public void vyplnObrazek(BufferedImage obrazek, int x, int y, Color barva) {
        if (barva == null) {
            barva = new Color(255, 255, 0);
        }

        // Zamez vyplnovani mimo rozsah
        if (x < 0 || x >= obrazek.getWidth() || y < 0 || y >= obrazek.getHeight()) {
            return;
        }

        WritableRaster pixely = obrazek.getRaster();
        int[] novyPixel = new int[] {barva.getRed(), barva.getGreen(), barva.getBlue(), barva.getAlpha()};
        int[] staryPixel = new int[] {255, 255, 255, 255};
        staryPixel = pixely.getPixel(x, y, staryPixel);

        // Pokud uz je pocatecni pixel obarven na cilovou barvu, nic nemen
        if (pixelyMajiStejnouBarvu(novyPixel, staryPixel)) {
            return;
        }

        // Zamez prebarveni cerne cary
        int[] cernyPixel = new int[] {0, 0, 0, staryPixel[3]};
        if (pixelyMajiStejnouBarvu(cernyPixel, staryPixel)) {
            return;
        }

        vyplnovaciSmycka(pixely, x, y, novyPixel, staryPixel);
    }

    /**
     * Provede skutecne vyplneni pomoci zasobniku
     */
    private void vyplnovaciSmycka(WritableRaster raster, int x, int y, int[] novaBarva, int[] nahrazovanaBarva) {
        Rectangle rozmery = raster.getBounds();
        int[] aktualniBarva = new int[] {255, 255, 255, 255};

        Deque<Point> zasobnik = new ArrayDeque<>(rozmery.width * rozmery.height);
        zasobnik.push(new Point(x, y));
        while (zasobnik.size() > 0) {
            Point point = zasobnik.pop();
            x = point.x;
            y = point.y;
            if (!pixelyMajiStejnouBarvu(raster.getPixel(x, y, aktualniBarva), nahrazovanaBarva)) {
                continue;
            }

            // Najdi levou zed, po ceste vyplnuj
            int levaZed = x;
            do {
                raster.setPixel(levaZed, y, novaBarva);
                levaZed--;
            }
            while (levaZed >= 0 && pixelyMajiStejnouBarvu(raster.getPixel(levaZed, y, aktualniBarva), nahrazovanaBarva));
            levaZed++;

            // Najdi pravou zed, po ceste vyplnuj
            int pravaZed = x;
            do {
                raster.setPixel(pravaZed, y, novaBarva);
                pravaZed++;
            }
            while (pravaZed < rozmery.width && pixelyMajiStejnouBarvu(raster.getPixel(pravaZed, y, aktualniBarva), nahrazovanaBarva));
            pravaZed--;

            // Pridej na zasobnik body nahore a dole
            for (int i = levaZed; i <= pravaZed; i++) {
                if (y > 0 && pixelyMajiStejnouBarvu(raster.getPixel(i, y - 1, aktualniBarva), nahrazovanaBarva)) {
                    if (!(i > levaZed && i < pravaZed
                            && pixelyMajiStejnouBarvu(raster.getPixel(i - 1, y - 1, aktualniBarva), nahrazovanaBarva)
                            && pixelyMajiStejnouBarvu(raster.getPixel(i + 1, y - 1, aktualniBarva), nahrazovanaBarva))) {
                        zasobnik.add(new Point(i, y - 1));
                    }
                }
                if (y < rozmery.height - 1 && pixelyMajiStejnouBarvu(raster.getPixel(i, y + 1, aktualniBarva), nahrazovanaBarva)) {
                    if (!(i > levaZed && i < pravaZed
                            && pixelyMajiStejnouBarvu(raster.getPixel(i - 1, y + 1, aktualniBarva), nahrazovanaBarva)
                            && pixelyMajiStejnouBarvu(raster.getPixel(i + 1, y + 1, aktualniBarva), nahrazovanaBarva))) {
                        zasobnik.add(new Point(i, y + 1));
                    }
                }
            }
        }
    }

    /**
     * Vrati true pokud RGB hodnoty v polich jsou stejne. Pokud jsou ruzne, vraci false
     */
    private boolean pixelyMajiStejnouBarvu(int[] barva1, int[] barva2) {
        return barva1[0] == barva2[0] && barva1[1] == barva2[1] && barva1[2] == barva2[2];
    }

    private void priStisknutiLabShadesOfGold(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 215, 0));
        labBarva2.setBackground(new Color(217, 184, 0));
        labBarva3.setBackground(new Color(179, 152, 0));
        labBarva4.setBackground(new Color(140, 119, 0));
        labBarva5.setBackground(new Color(102, 87, 0));
    }

    private void priStisknutiLabShadesOfCrimson(MouseEvent e) {
        labBarva1.setBackground(new Color(220, 20, 60));
        labBarva2.setBackground(new Color(181, 16, 49));
        labBarva3.setBackground(new Color(143, 13, 39));
        labBarva4.setBackground(new Color(105, 9, 28));
        labBarva5.setBackground(new Color(66, 6, 18));
    }

    private void priStisknutiLabShadesOfOrange(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 135, 0));
        labBarva2.setBackground(new Color(217, 116, 0));
        labBarva3.setBackground(new Color(179, 95, 0));
        labBarva4.setBackground(new Color(140, 75, 0));
        labBarva5.setBackground(new Color(102, 54, 0));
    }

    private void priStisknutoLabShadesOfRed(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 0, 0));
        labBarva2.setBackground(new Color(217, 0, 0));
        labBarva3.setBackground(new Color(179, 0, 0));
        labBarva4.setBackground(new Color(140, 0, 0));
        labBarva5.setBackground(new Color(102, 0, 0));
    }

    private void priStisknutiLabShadesOfDeepPink(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 20, 147));
        labBarva2.setBackground(new Color(217, 17, 124));
        labBarva3.setBackground(new Color(179, 14, 102));
        labBarva4.setBackground(new Color(140, 11, 80));
        labBarva5.setBackground(new Color(102, 8, 58));
    }

    private void priStisknutiLabShadesOfPurple(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 0, 255));
        labBarva2.setBackground(new Color(217, 0, 217));
        labBarva3.setBackground(new Color(179, 0, 179));
        labBarva4.setBackground(new Color(140, 0, 140));
        labBarva5.setBackground(new Color(102, 0, 102));
    }

    private void priStisknutiLabShadesOfDarkOrchid(MouseEvent e) {
        labBarva1.setBackground(new Color(191, 64, 255));
        labBarva2.setBackground(new Color(163, 54, 217));
        labBarva3.setBackground(new Color(134, 45, 179));
        labBarva4.setBackground(new Color(105, 35, 140));
        labBarva5.setBackground(new Color(77, 26, 102));
    }

    private void priStisknutiLabShadesOfBlue(MouseEvent e) {
        labBarva1.setBackground(new Color(0, 0, 255));
        labBarva2.setBackground(new Color(0, 0, 217));
        labBarva3.setBackground(new Color(0, 0, 179));
        labBarva4.setBackground(new Color(0, 0, 140));
        labBarva5.setBackground(new Color(0, 0, 102));
    }

    private void priStisknutiLabShadesOfDeepSkyBlue(MouseEvent e) {
        labBarva1.setBackground(new Color(0, 191, 255));
        labBarva2.setBackground(new Color(0, 163, 217));
        labBarva3.setBackground(new Color(0, 134, 179));
        labBarva4.setBackground(new Color(0, 105, 140));
        labBarva5.setBackground(new Color(0, 77, 102));
    }

    private void priStisknutiLabShadesOfAqua(MouseEvent e) {
        labBarva1.setBackground(new Color(0, 255, 255));
        labBarva2.setBackground(new Color(0, 217, 217));
        labBarva3.setBackground(new Color(0, 179, 179));
        labBarva4.setBackground(new Color(0, 140, 140));
        labBarva5.setBackground(new Color(0, 102, 102));
    }

    private void priStisknutiLabShadesOfSpringGreen(MouseEvent e) {
        labBarva1.setBackground(new Color(0, 255, 128));
        labBarva2.setBackground(new Color(0, 217, 108));
        labBarva3.setBackground(new Color(0, 179, 89));
        labBarva4.setBackground(new Color(0, 140, 70));
        labBarva5.setBackground(new Color(0, 102, 51));
    }

    private void priStisknutiLabShadesOfForestGreen(MouseEvent e) {
        labBarva1.setBackground(new Color(58, 242, 58));
        labBarva2.setBackground(new Color(34, 139, 34));
        labBarva3.setBackground(new Color(24, 102, 24));
        labBarva4.setBackground(new Color(15, 64, 15));
        labBarva5.setBackground(new Color(6, 26, 6));
    }

    private void priStisknutiLabShadesOfLime(MouseEvent e) {
        labBarva1.setBackground(new Color(0, 255, 0));
        labBarva2.setBackground(new Color(0, 217, 0));
        labBarva3.setBackground(new Color(0, 179, 0));
        labBarva4.setBackground(new Color(0, 140, 0));
        labBarva5.setBackground(new Color(0, 102, 0));
    }

    private void priStisknutiLabShadesOfYellow(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 255, 0));
        labBarva2.setBackground(new Color(217, 217, 0));
        labBarva3.setBackground(new Color(179, 179, 0));
        labBarva4.setBackground(new Color(140, 140, 0));
        labBarva5.setBackground(new Color(102, 102, 0));
    }

    private void priStisknutiLabShadesOfBlack(MouseEvent e) {
        labBarva1.setBackground(new Color(255, 255, 255));
        labBarva2.setBackground(new Color(192, 192, 192));
        labBarva3.setBackground(new Color(128, 128, 128));
        labBarva4.setBackground(new Color(64, 64, 64));
        labBarva5.setBackground(new Color(0, 0, 0));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        menuBar1 = new JMenuBar();
        labOpen = new JLabel();
        labSaveAs = new JLabel();
        menuBar2 = new JMenuBar();
        labShadesOfBlack = new JLabel();
        labShadesOfYellow = new JLabel();
        labShadesOfGold = new JLabel();
        labShadesOfOrange = new JLabel();
        labShadesOfRed = new JLabel();
        labShadesOfCrimson = new JLabel();
        labShadesOfDeepPink = new JLabel();
        labShadesOfPurple = new JLabel();
        menuBar3 = new JMenuBar();
        labShadesOfDarkOrchid = new JLabel();
        labShadesOfBlue = new JLabel();
        labShadesOfDeepSkyBlue = new JLabel();
        labShadesOfAqua = new JLabel();
        labShadesOfSpringGreen = new JLabel();
        labShadesOfForestGreen = new JLabel();
        labShadesOfLime = new JLabel();
        labAktualniBarva = new JLabel();
        labBarva1 = new JLabel();
        labBarva2 = new JLabel();
        labBarva3 = new JLabel();
        labBarva4 = new JLabel();
        labBarva5 = new JLabel();
        scrollPaneObrazku = new JScrollPane();
        labObrazek = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mandala");
        setMinimumSize(null);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets rel,hidemode 3",
            // columns
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[fill]" +
            "[]"));
        this.contentPane = (JPanel) this.getContentPane();
        this.contentPane.setBackground(this.getBackground());
        LayoutManager layout = this.contentPane.getLayout();
        if (layout instanceof MigLayout) {
            this.migLayoutManager = (MigLayout) layout;
        }

        //======== menuBar1 ========
        {

            //---- labOpen ----
            labOpen.setText("Open");
            labOpen.setHorizontalAlignment(SwingConstants.LEFT);
            labOpen.setFont(labOpen.getFont().deriveFont(labOpen.getFont().getSize() + 2f));
            labOpen.setBorder(UIManager.getBorder("Button.border"));
            labOpen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabOpen(e);
                }
            });
            menuBar1.add(labOpen);

            //---- labSaveAs ----
            labSaveAs.setText("Save as");
            labSaveAs.setHorizontalAlignment(SwingConstants.LEFT);
            labSaveAs.setFont(labSaveAs.getFont().deriveFont(labSaveAs.getFont().getSize() + 2f));
            labSaveAs.setBorder(UIManager.getBorder("Button.border"));
            labSaveAs.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabSaveAs(e);
                }
            });
            menuBar1.add(labSaveAs);
        }
        setJMenuBar(menuBar1);

        //======== menuBar2 ========
        {

            //---- labShadesOfBlack ----
            labShadesOfBlack.setText("Shades of Black");
            labShadesOfBlack.setFont(labShadesOfBlack.getFont().deriveFont(labShadesOfBlack.getFont().getSize() + 1f));
            labShadesOfBlack.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfBlack.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfBlack.setMinimumSize(new Dimension(101, 20));
            labShadesOfBlack.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfBlack(e);
                }
            });
            menuBar2.add(labShadesOfBlack);

            //---- labShadesOfYellow ----
            labShadesOfYellow.setText("Shades of Yellow");
            labShadesOfYellow.setFont(labShadesOfYellow.getFont().deriveFont(labShadesOfYellow.getFont().getSize() + 1f));
            labShadesOfYellow.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfYellow.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfYellow.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfYellow(e);
                }
            });
            menuBar2.add(labShadesOfYellow);

            //---- labShadesOfGold ----
            labShadesOfGold.setText("Shades of Gold");
            labShadesOfGold.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfGold.setFont(labShadesOfGold.getFont().deriveFont(labShadesOfGold.getFont().getSize() + 1f));
            labShadesOfGold.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfGold.setMinimumSize(new Dimension(101, 20));
            labShadesOfGold.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfGold(e);
                }
            });
            menuBar2.add(labShadesOfGold);

            //---- labShadesOfOrange ----
            labShadesOfOrange.setText("Shades of Orange");
            labShadesOfOrange.setFont(labShadesOfOrange.getFont().deriveFont(labShadesOfOrange.getFont().getSize() + 1f));
            labShadesOfOrange.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfOrange.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfOrange.setMinimumSize(new Dimension(101, 20));
            labShadesOfOrange.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfOrange(e);
                }
            });
            menuBar2.add(labShadesOfOrange);

            //---- labShadesOfRed ----
            labShadesOfRed.setText("Shades of Red");
            labShadesOfRed.setFont(labShadesOfRed.getFont().deriveFont(labShadesOfRed.getFont().getSize() + 1f));
            labShadesOfRed.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfRed.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfRed.setMinimumSize(new Dimension(101, 20));
            labShadesOfRed.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutoLabShadesOfRed(e);
                }
            });
            menuBar2.add(labShadesOfRed);

            //---- labShadesOfCrimson ----
            labShadesOfCrimson.setText("Shades of Crimson");
            labShadesOfCrimson.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfCrimson.setFont(labShadesOfCrimson.getFont().deriveFont(labShadesOfCrimson.getFont().getSize() + 1f));
            labShadesOfCrimson.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfCrimson.setMinimumSize(new Dimension(101, 20));
            labShadesOfCrimson.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfCrimson(e);
                }
            });
            menuBar2.add(labShadesOfCrimson);

            //---- labShadesOfDeepPink ----
            labShadesOfDeepPink.setText("Shades Of Deep Pink");
            labShadesOfDeepPink.setFont(labShadesOfDeepPink.getFont().deriveFont(labShadesOfDeepPink.getFont().getSize() + 1f));
            labShadesOfDeepPink.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfDeepPink.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfDeepPink.setMinimumSize(new Dimension(101, 20));
            labShadesOfDeepPink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfDeepPink(e);
                }
            });
            menuBar2.add(labShadesOfDeepPink);

            //---- labShadesOfPurple ----
            labShadesOfPurple.setText("Shades of Purple");
            labShadesOfPurple.setFont(labShadesOfPurple.getFont().deriveFont(labShadesOfPurple.getFont().getSize() + 1f));
            labShadesOfPurple.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfPurple.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfPurple.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfPurple(e);
                }
            });
            menuBar2.add(labShadesOfPurple);
        }
        contentPane.add(menuBar2, "pad 0,cell 0 0 8 1,gapy 0 0");

        //======== menuBar3 ========
        {

            //---- labShadesOfDarkOrchid ----
            labShadesOfDarkOrchid.setText("Shades of Dark Orchid");
            labShadesOfDarkOrchid.setFont(labShadesOfDarkOrchid.getFont().deriveFont(labShadesOfDarkOrchid.getFont().getSize() + 1f));
            labShadesOfDarkOrchid.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfDarkOrchid.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfDarkOrchid.setMinimumSize(new Dimension(101, 20));
            labShadesOfDarkOrchid.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfDarkOrchid(e);
                }
            });
            menuBar3.add(labShadesOfDarkOrchid);

            //---- labShadesOfBlue ----
            labShadesOfBlue.setText("Shades of Blue");
            labShadesOfBlue.setFont(labShadesOfBlue.getFont().deriveFont(labShadesOfBlue.getFont().getSize() + 1f));
            labShadesOfBlue.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfBlue.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfBlue.setMinimumSize(new Dimension(101, 20));
            labShadesOfBlue.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfBlue(e);
                }
            });
            menuBar3.add(labShadesOfBlue);

            //---- labShadesOfDeepSkyBlue ----
            labShadesOfDeepSkyBlue.setText("Shades of Deep Sky Blue");
            labShadesOfDeepSkyBlue.setFont(labShadesOfDeepSkyBlue.getFont().deriveFont(labShadesOfDeepSkyBlue.getFont().getSize() + 1f));
            labShadesOfDeepSkyBlue.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfDeepSkyBlue.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfDeepSkyBlue.setMinimumSize(new Dimension(101, 20));
            labShadesOfDeepSkyBlue.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfDeepSkyBlue(e);
                }
            });
            menuBar3.add(labShadesOfDeepSkyBlue);

            //---- labShadesOfAqua ----
            labShadesOfAqua.setText("Shades of Aqua");
            labShadesOfAqua.setFont(labShadesOfAqua.getFont().deriveFont(labShadesOfAqua.getFont().getSize() + 1f));
            labShadesOfAqua.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfAqua.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfAqua.setMinimumSize(new Dimension(101, 20));
            labShadesOfAqua.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfAqua(e);
                }
            });
            menuBar3.add(labShadesOfAqua);

            //---- labShadesOfSpringGreen ----
            labShadesOfSpringGreen.setText("Shades of Spring Green");
            labShadesOfSpringGreen.setFont(labShadesOfSpringGreen.getFont().deriveFont(labShadesOfSpringGreen.getFont().getSize() + 1f));
            labShadesOfSpringGreen.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfSpringGreen.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfSpringGreen.setMinimumSize(new Dimension(101, 20));
            labShadesOfSpringGreen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfSpringGreen(e);
                }
            });
            menuBar3.add(labShadesOfSpringGreen);

            //---- labShadesOfForestGreen ----
            labShadesOfForestGreen.setText("Shades of Forest Green");
            labShadesOfForestGreen.setFont(labShadesOfForestGreen.getFont().deriveFont(labShadesOfForestGreen.getFont().getSize() + 1f));
            labShadesOfForestGreen.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfForestGreen.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfForestGreen.setMinimumSize(new Dimension(101, 20));
            labShadesOfForestGreen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfForestGreen(e);
                }
            });
            menuBar3.add(labShadesOfForestGreen);

            //---- labShadesOfLime ----
            labShadesOfLime.setText("Shades of Lime");
            labShadesOfLime.setFont(labShadesOfLime.getFont().deriveFont(labShadesOfLime.getFont().getSize() + 1f));
            labShadesOfLime.setHorizontalAlignment(SwingConstants.LEFT);
            labShadesOfLime.setBorder(UIManager.getBorder("Button.border"));
            labShadesOfLime.setMinimumSize(new Dimension(101, 20));
            labShadesOfLime.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiLabShadesOfLime(e);
                }
            });
            menuBar3.add(labShadesOfLime);
        }
        contentPane.add(menuBar3, "pad 0,cell 0 1 8 1,gapy 0 0");

        //---- labAktualniBarva ----
        labAktualniBarva.setText("Shades:");
        contentPane.add(labAktualniBarva, "cell 1 2");

        //---- labBarva1 ----
        labBarva1.setMinimumSize(new Dimension(32, 32));
        labBarva1.setOpaque(true);
        labBarva1.setBackground(new Color(204, 204, 204));
        labBarva1.setBorder(UIManager.getBorder("CheckBox.border"));
        labBarva1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                priStisknutiLabBarva(e);
            }
        });
        contentPane.add(labBarva1, "cell 2 2");

        //---- labBarva2 ----
        labBarva2.setMinimumSize(new Dimension(32, 32));
        labBarva2.setOpaque(true);
        labBarva2.setBackground(new Color(204, 204, 204));
        labBarva2.setBorder(UIManager.getBorder("CheckBox.border"));
        labBarva2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                priStisknutiLabBarva(e);
            }
        });
        contentPane.add(labBarva2, "cell 3 2");

        //---- labBarva3 ----
        labBarva3.setMinimumSize(new Dimension(32, 32));
        labBarva3.setOpaque(true);
        labBarva3.setBackground(new Color(204, 204, 204));
        labBarva3.setBorder(UIManager.getBorder("CheckBox.border"));
        labBarva3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                priStisknutiLabBarva(e);
            }
        });
        contentPane.add(labBarva3, "cell 4 2");

        //---- labBarva4 ----
        labBarva4.setMinimumSize(new Dimension(32, 32));
        labBarva4.setOpaque(true);
        labBarva4.setBackground(new Color(204, 204, 204));
        labBarva4.setBorder(UIManager.getBorder("CheckBox.border"));
        labBarva4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                priStisknutiLabBarva(e);
            }
        });
        contentPane.add(labBarva4, "cell 5 2");

        //---- labBarva5 ----
        labBarva5.setMinimumSize(new Dimension(32, 32));
        labBarva5.setOpaque(true);
        labBarva5.setBackground(new Color(204, 204, 204));
        labBarva5.setBorder(UIManager.getBorder("CheckBox.border"));
        labBarva5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                priStisknutiLabBarva(e);
            }
        });
        contentPane.add(labBarva5, "cell 6 2");

        //======== scrollPaneObrazku ========
        {

            //---- labObrazek ----
            labObrazek.setHorizontalAlignment(SwingConstants.LEFT);
            labObrazek.setVerticalAlignment(SwingConstants.TOP);
            labObrazek.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    priStisknutiMysiNadLabObrazek(e);
                }
            });
            scrollPaneObrazku.setViewportView(labObrazek);
        }
        contentPane.add(scrollPaneObrazku, "cell 0 3 8 1,alignx left,growx 0");
        setSize(875, 370);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}
