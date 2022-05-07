import java.awt.BorderLayout; //��ͺ
import java.awt.Color; //��
import java.awt.Dimension; //��Ѻ��Ҵ���Ҿ
import java.awt.Font; //���͡�͹��
import java.awt.FontMetrics; //����ʴ���ͤ���
import java.awt.Graphics; 
import java.awt.Graphics2D;  
import java.awt.RenderingHints;  
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent; //�Ѻ���˹觡�ä��ꡢͧ�����
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities; //��㹡���Ѿവ
import java.awt.event.*;
import java.text.*;
public class FifteenPuzzel extends JPanel { 
  
  private int size;
  private int numberTiles;
  private int dimension;
  private static final Color FOREGROUND_COLOR = new Color(29,200,143);
  private static final Random RANDOM = new Random();
  private int[] tiles;
  private int tileSize;
  private int blankPos;
  private int margin;
  private int gridSize;
  private boolean gameOver; 
  public static javax.swing.Timer t;
  static int minute, second;
  
  public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> { //���Ѿവ
       JFrame frame = new JFrame();
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setTitle("Fifteen Puzzel"); 
       frame.setResizable(false); //������͢���˹�ҨͶ����false��������͢��������
       frame.add(new FifteenPuzzel(4, 550, 30), BorderLayout.CENTER); //�������������������ͷGameOfFifteen
       frame.pack(); //�繡��set��Ҵ�ͧ frame
       frame.setLocationRelativeTo(null); //૵�������Թ������ç��ҧ�ͧ˹�Ҩ�
       frame.setVisible(true); //�ʴ�˹�ҵ�ҧ
       final DecimalFormat dc = new DecimalFormat("00"); //��С����ҵ��˹觹ҷ�����Թҷ����ٻẺ�ȹ���
       t = new javax.swing.Timer( 
             1000, //��˹�˹������� 1 �Թҷ� = 1000 ������Թҷ�(Timer �ӧҹ�ء�1�Թҷ�)
             new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     frame.setTitle(dc.format(minute) + ":" + dc.format(second)); //�ʴ� string ����絤�����㹵���� dc
                     second++;
                     if (second >= 60) 
                     {
                         second %= 60;
                         minute++;
                    }
                 }
             }
         ); 
     });
  }
  
  public FifteenPuzzel(int size, int dim, int mar) {
    this.size = size;
    dimension = dim;
    margin = mar;
    
    numberTiles = size * size - 1; 
    tiles = new int[size * size]; //������1�ԵԢͧ����Ţ1-15
    
    gridSize = (dim - 2 * margin); //�����ҧ
    tileSize = gridSize / size; //����ͺ���С�ͺ
    
    setPreferredSize(new Dimension(dimension, dimension + margin)); //set ��Ҵ window
    setBackground(Color.WHITE); 
    setForeground(FOREGROUND_COLOR);
    setFont(new Font("SansSerif", Font.BOLD, 60)); //set font �ͧ����Ţ����к��ͤ����Ţ
    
    gameOver = true;
    
    addMouseListener(new MouseAdapter() //��Ǩ�Ѻ��� click �ͧ����� 
    {
      public void mousePressed(MouseEvent e) {
        if (gameOver) {
          newGame();
          minute = 0;second = 0;
          t.start();
        } else {
          t.start();
          //�ҵ��˹觷����������
          int ex = e.getX() - margin;
          int ey = e.getY() - margin;
          
          //����ҵ��˹觷��������躹���ҧ�������
          if (ex < 0 || ex > gridSize  || ey < 0  || ey > gridSize)
            return;
         
          int c1 = ex / tileSize;
          int r1 = ey / tileSize;
          
          int c2 = blankPos % size;
          int r2 = blankPos / size;
          
          int clickPos = r1 * size + c1;
          
          int dir = 0;
          
          //�����繤�ҵ��˹��դ���繺ǡ
          if (c1 == c2  &&  Math.abs(r1 - r2) > 0)
            dir = (r1 - r2) > 0 ? size : -size; //��Ѻ᡹_y
          else if (r1 == r2 && Math.abs(c1 - c2) > 0)
            dir = (c1 - c2) > 0 ? 1 : -1; //��Ѻ᡹_x
            
          if (dir != 0) {
            do {
              int newBlankPos = blankPos + dir;
              tiles[blankPos] = tiles[newBlankPos];
              blankPos = newBlankPos;
            } while(blankPos != clickPos);
            
            tiles[blankPos] = 0;
          }

          if(gameOver = isSolved()) t.stop();
        }
        
        repaint();
      }
    });
    
    newGame();
  }
  
  //�����isSolvable == true����������� 
  private void newGame() {
    do {
      reset(); 
      shuffle(); 
    } while(!isSolvable()); 
    
    gameOver = false;
  }
  
  private void reset() {
    for (int i = 0; i < tiles.length; i++) {
      tiles[i] = (i + 1) % tiles.length;
    }
    
    blankPos = tiles.length - 1;
  }
  
  private void shuffle() {
    int n = numberTiles;
    
    while (n > 1) {
      int r = RANDOM.nextInt(n--);
      int tmp = tiles[r];
      tiles[r] = tiles[n];
      tiles[n] = tmp;
    }
  }
  
  //����������������
  private boolean isSolvable() {
    int countInversions = 0;
    
    for (int i = 0; i < numberTiles; i++) {
      for (int j = 0; j < i; j++) {
        if (tiles[j] > tiles[i])
          countInversions++;
      }
    }
    
    return countInversions % 2 == 0;
  }
  
  //����������������ѧ
  private boolean isSolved() {
    if (tiles[tiles.length - 1] != 0) 
      return false;
    
    for (int i = numberTiles - 1; i >= 0; i--) {
      if (tiles[i] != i + 1)
        return false;      
    }
    
    return true;
  }
  
  private void drawGrid(Graphics2D g) {
    for (int i = 0; i < tiles.length; i++) {      
      int r = i / size;
      int c = i % size;
      //�ԡѴ�ͧ���ͤ
      int x = margin + c * tileSize;
      int y = margin + r * tileSize;
      
      if(tiles[i] == 0) {
        if (gameOver) {
          g.setColor(FOREGROUND_COLOR);
          drawCenteredString(g, "\u2713", x, y); //\u2764����ٻ����\u2713�����ٻ��꡶١
        }
        
        continue;
      }
      
      g.setColor(getForeground()); //set �բͧ���к��ͤ
      g.fillRoundRect(x, y, tileSize, tileSize, 55, 55); //��Ѻ�������ͧ��ͺ���ͤ
      g.setColor(Color.WHITE); //�ա�ͺ�ͧ���к��ͤ
      g.drawRoundRect(x, y, tileSize, tileSize, 55, 55); //��Ѻ�������ͧ��鹺��ͤ
      g.setColor(Color.WHITE); //�յ���Ţ
      
      drawCenteredString(g, String.valueOf(tiles[i]), x , y); 
    }
  }
  
  private void drawCenteredString(Graphics2D g, String s, int x, int y) {
     FontMetrics fm = g.getFontMetrics();
     int asc = fm.getAscent();
     int desc = fm.getDescent();
     g.drawString(s,  x + (tileSize - fm.stringWidth(s)) / 2, 
         y + (asc + (tileSize - (asc + desc)) / 2));
   }
  
  private void drawStartMessage(Graphics2D g) {
    if (gameOver) {
      g.setFont(getFont().deriveFont(Font.BOLD, 18));
      g.setColor(FOREGROUND_COLOR);
      String s = "Click to start new game";
      g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2, getHeight() - margin);
      
    }
  }
  
  protected void paintComponent(Graphics g) 
  {
    super.paintComponent(g); //�����ͤ��������㹡�� paint
    Graphics2D g2D = (Graphics2D) g;
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g2D);
    drawStartMessage(g2D);
  }
  
}