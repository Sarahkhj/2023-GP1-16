package com.example.myapplication324;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class WheelView extends View {
    private static final int NUM_SECTORS = 8;
    private static final int EDGE_COLOR = Color.RED;
    private static final int EDGE_WIDTH = 20;
    private static final String[] ALPHABETS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            ".", "/"
    };    private static final int[] SECTOR_COLORS = {
            Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.RED,
            Color.rgb(128, 0, 128), Color.DKGRAY
    };
    private static final int LINE_COLOR = Color.BLACK;
    private static final int LINE_WIDTH = 5;
    private static final int POSITION_COLOR = Color.BLACK;
    private int I =0;
    private int usertry=0;


    private Paint edgePaint;
    private Paint sectorPaint;
    private Paint linePaint;
    private Paint textPaint;
    private Random random;
    private RectF sectorRect;
    private float rotationAngle;
    private int selectedSector = -1;
    private TextView textView;

    public WheelView(Context context) {
        super(context);
        init();
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }


    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        edgePaint = new Paint();
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(EDGE_WIDTH);
        edgePaint.setColor(EDGE_COLOR);

        sectorPaint = new Paint();
        sectorPaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);
        linePaint.setColor(LINE_COLOR);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
//        textPaint.setTextAlign(Paint.Align.LEFT);

        random = new Random();
        sectorRect = new RectF();
        shuffleAlphabets();

    }
    private void shuffleAlphabets() {
        List<String> alphabetList = new ArrayList<>(Arrays.asList(ALPHABETS));
        Collections.shuffle(alphabetList);
        alphabetList.toArray(ALPHABETS);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // Set the background color to white
        canvas.drawColor(Color.WHITE);

        int NUM_SECTORS = 8; // Number of sectors
        int NUM_POSITIONS = 8; // Number of positions inside each sector

        int startAngle = 0;
        int sweepAngle = 360 / NUM_SECTORS;

        for (int i = 0; i < NUM_SECTORS; i++) {
            int endAngle = startAngle + sweepAngle;

            // Draw the sector
            Paint edgePaint = new Paint();
            edgePaint.setColor(SECTOR_COLORS[i % SECTOR_COLORS.length]);
            edgePaint.setStrokeWidth(8);
            edgePaint.setStyle(Paint.Style.STROKE);

            sectorRect.set(
                    width / 2 - radius,
                    height / 2 - radius,
                    width / 2 + radius,
                    height / 2 + radius
            );
            canvas.drawArc(sectorRect, startAngle, sweepAngle, true, edgePaint);

            // Calculate the angle between each position inside the sector
            int numPositions = NUM_POSITIONS;
            int positionAngle = sweepAngle / numPositions;

            // Draw positions inside the sector
            for (int j = 0; j < numPositions; j++) {
                int positionStartAngle = startAngle + (j * positionAngle) + (int) rotationAngle;
                int positionEndAngle = positionStartAngle + positionAngle;
                // Calculate the position coordinates
                float positionMidAngle = (startAngle + endAngle) / 2f-rotationAngle ;

                float positionX, positionY;
                float spacing= 60f;

                if (j < 4) {
                    float characterAngle = positionMidAngle -8+ (j * 7.8f);
                    // 4 characters positioned close to the arc
                    float distanceFromArc = (float) (radius * 0.89); // Adjust the distance as desired
                    float offset = (float) (radius * 0.09); // Adjust the offset as desired
                    positionX = (float) (width / 2 + distanceFromArc * Math.cos(Math.toRadians( positionMidAngle -10+ (j * 7.8f))))  ;
                    positionY = (float) (height / 2 + distanceFromArc * Math.sin(Math.toRadians( positionMidAngle -10+ (j * 7.8f)))) ;
                } else if (j < 7) {
                    //  float characterAngle = positionMidAngle -7 + (j * 10);
                    // 3 characters positioned after the 4 characters
                    float distanceFromArc = (float) (radius * 0.69); // Adjust the distance as desired
                    float offset = (float) (radius * 0.01); // Adjust the offset as desired
                    positionX = (float) (width / 2 + distanceFromArc * Math.cos(Math.toRadians(positionMidAngle -38 + (j * 7f)))) ;
                    positionY = (float) (height / 2 + distanceFromArc * Math.sin(Math.toRadians(positionMidAngle -38 + (j * 8f))));
                } else {
                    // 1 character positioned towards the inner sector
                    float distanceFromArc = (float) (radius * 0.45); // Adjust the distance as desired
                    float characterAngle = positionMidAngle +12 + (j * 10);
                    float offset = (float) (radius * 0.08f); // Adjust the offset as desired
                    positionX = (float) (width / 2 + distanceFromArc * Math.cos(Math.toRadians(positionMidAngle-14+(j*2)))) ;
                    positionY = (float) (height / 2 + distanceFromArc * Math.sin(Math.toRadians(positionMidAngle-14+(j*2))));
                }
                // Calculate the size of the character based on the position radius
                float positionSize = (float) (radius * 7);



                // Draw the position number at the centered position
                String character = ALPHABETS[(i * numPositions + j) % ALPHABETS.length];
                Paint textPaint = new Paint();
                textPaint.setColor(POSITION_COLOR);
                textPaint.setTextSize(55); // Adjust the text size as desired
                textPaint.setTextAlign(Paint.Align.CENTER);
// Adjust the text size as desired
                // Adjust the text size and font style based on the character
                if (character.equals(".")) {
                    textPaint.setTextSize(70); // Increase the text size for "."
                    textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else if (character.equals("/")) {
                    textPaint.setTextSize(65); // Default text size for "/"
                    textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                } else {
                    textPaint.setTextSize(55); // Default text size for other characters
                }
                // Apply font styles based on the type of character
                if (Character.isUpperCase(character.charAt(0))) {
                    textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else if (Character.isDigit(character.charAt(0))) {
                    textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                }


                // Draw the character
                for (int k = 0; k < character.length(); k++) {
                    canvas.drawText(String.valueOf(character.charAt(k)), positionX+1, positionY+1, textPaint);
                    positionX += positionSize; // Move to the next position
                }


            }

            startAngle = endAngle;
        }
    }



    public boolean onTouchEvent(MotionEvent event,int Color, String realPass) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int newlySelectedSector = getTouchedSector(x, y);

            if (newlySelectedSector != -1) {

                int colorIndex = (newlySelectedSector + (NUM_SECTORS - (int) (rotationAngle / (360f / NUM_SECTORS)))) % NUM_SECTORS;
                int sectorColor = SECTOR_COLORS[colorIndex % SECTOR_COLORS.length];

                if (sectorColor == Color) {
                   // String colorName = getColorName(sectorColor);

                    // Calculate the starting index of the selected sector in ALPHABETS
                    int sectorStartIndex = newlySelectedSector * 8;

                    StringBuilder selectedLetters = new StringBuilder();
                    for (int i = 0; i < 8; i++) {
                        int letterIndex = (sectorStartIndex + i) % ALPHABETS.length;
                        selectedLetters.append(ALPHABETS[letterIndex]+"-");
                    }

                   // String message = "Selected letters: " + selectedLetters + ", Color: " + colorName;

                   // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (textView != null) {
                        String currentText = textView.getText().toString();
                        String newText = currentText + selectedLetters.toString();
                        String All = textView.getText().toString();

                        //realPass = "Ss1Hlm";
                      //  if(usertry!=3){
                            if (checkpassword(newText, I,realPass)) {
                                currentText += realPass.charAt(I);
                                textView.setText(currentText);
                                if (realPass.length() - 1 <= I) {
                                   // Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                    //textView.post(() -> textView.setText(realPass));

                                } else {
                                    I++;
                                   // Toast.makeText(getContext(), "True", Toast.LENGTH_SHORT).show();
                                    shuffleAlphabets();
                                    invalidate();
                                }
                            } else {
                                I = 0;
                                currentText += realPass.charAt(I);
                                textView.setText(currentText);
                               // usertry++;
                                shuffleAlphabets();
                                invalidate();
                            }
                        //}
                     //   else {
                          //  showAlertDialog("Max Attempts Exceeded", "You have exceeded the maximum number of login attempts. Do you want to reset your password?");
                       // }
                    }
                }

                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private int getTouchedSector(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        float centerX = width / 2f;
        float centerY = height / 2f;

        // Calculate the angle of the touched point with respect to the center of the wheel
        double angle = Math.toDegrees(Math.atan2(y - centerY, x - centerX));
        angle = (angle < 0) ? (360 + angle) : angle;

        // Adjust for the current rotation angle
        angle += rotationAngle;
        angle = (angle < 0) ? (360 + angle) : angle;

        // Calculate the selected sector based on the angle
        int sector = (int) (angle * NUM_SECTORS / 360);
        return sector;
    }





    private String getColorName(int color) {
        if (color == Color.BLUE) {
            return "Blue";
        } else if (color == Color.GREEN) {
            return "Green";
        } else if (color == Color.YELLOW) {
            return "Yellow";
        } else if (color == Color.MAGENTA) {
            return "Magenta";
        } else if (color == Color.CYAN) {
            return "Cyan";
        } else if (color == Color.RED) {
            return "Red";
        } else if (color == Color.rgb(128, 0, 128)) {
            return "purple";
        } else if (color == Color.DKGRAY) {
            return "Dark Gray";
        } else {
            return "Unknown";
        }
    }







    public void rotateClockwise() {
        ValueAnimator animator = ValueAnimator.ofFloat(rotationAngle, rotationAngle + 45);
        animator.setDuration(500); // Set the duration of the animation in milliseconds

        animator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate(); // Invalidate the view to trigger redraw
        });

        animator.start();
    }

    public void rotateCounterClockwise() {
        ValueAnimator animator = ValueAnimator.ofFloat(rotationAngle, rotationAngle - 45);
        animator.setDuration(500); // Set the duration of the animation in milliseconds

        animator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate(); // Invalidate the view to trigger redraw
        });

        animator.start();
    }

    public boolean checkpassword(String x,int y,String realpass ){
        int i =y;
        // String realpass = "Ss1Hlm";
        String pass = x;
        if (realpass.length() > 0 && pass.length() > 0 &&i<=realpass.length()) {
            char firstLetter = realpass.charAt(i);
            return pass.contains(String.valueOf(firstLetter));
        }
        return false;
    }

    public void showAlertDialog(String title, String message,final Class<?> targetActivityClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle the click on the positive button (in this case, "OK").
                        dialog.dismiss(); // Close the dialog
                        Intent intent = new Intent(getContext(), targetActivityClass);
                        getContext().startActivity(intent);
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }






}