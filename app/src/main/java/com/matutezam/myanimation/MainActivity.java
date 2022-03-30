package com.matutezam.myanimation;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImageView> imageViews;
    private ArrayList<Integer> myImageList;
    private Animation hitAnimation;
    private Animation failAnimation;
    private Animation hitChangeAnimation;
    private Animation failChangeAnimation;
    private int[] randomIdImages;
    private ImageView clock;
    private ImageView clockByFrames;
    private ImageView goldCup;
    private AnimationDrawable frameAnimation;
    private Timer timer;
    private Integer imageId1;
    private Integer imageId2;
    private boolean start;
    private int hitCounter;
    private boolean fail;
    private boolean onEnable;
    private boolean onDisable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hitAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        hitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!onDisable) {
                    onDisable = true;
                    disableImages(imageViews);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(onDisable) {
                    onDisable = false;
                    if(hitCounter != 4) {
                        initImages(imageViews);
                    }
                    else
                    {
                        endOfGame();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        hitChangeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        hitChangeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!onEnable) {
                    onEnable = true;
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(onEnable) {
                    onEnable = false;
                    enableImages(imageViews);
                    chooseRandomImages(myImageList, randomIdImages);
                    imageId1 = -1;
                    imageId2 = -1;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        failAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        failAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!onDisable) {
                    onDisable = true;
                    disableImages(imageViews);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(onDisable) {
                    onDisable = false;
                    frameAnimation = (AnimationDrawable) clockByFrames.getBackground();
                    clock.setRotation(0);
                    initImages(imageViews);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        failChangeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        failChangeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!onEnable) {
                    onEnable = true;
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(onEnable) {
                    onEnable = false;
                    enableImages(imageViews);
                    chooseRandomImages(myImageList, randomIdImages);
                    imageId1 = -1;
                    imageId2 = -1;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        myImageList = new ArrayList<>();
        listResources(myImageList);

        imageViews = new ArrayList<>();
        imageViews.add(findViewById(R.id.imageView0));
        imageViews.add(findViewById(R.id.imageView1));
        imageViews.add(findViewById(R.id.imageView2));
        imageViews.add(findViewById(R.id.imageView3));

        imageId1 = -1;
        imageId2 = -1;
        clock = findViewById(R.id.clock);

        clockByFrames = findViewById(R.id.clockByFrames);
        clockByFrames.setVisibility(View.INVISIBLE);
        clockByFrames.setEnabled(false);
        clockByFrames.setBackgroundResource(R.drawable.clock_animation);
        frameAnimation = (AnimationDrawable) clockByFrames.getBackground();

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.setVisibility(View.INVISIBLE);
                clock.setEnabled(false);
                clockByFrames.setVisibility(View.VISIBLE);
                clockByFrames.setEnabled(true);
            }
        });

        clockByFrames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockByFrames.setVisibility(View.INVISIBLE);
                clockByFrames.setEnabled(false);
                clock.setVisibility(View.VISIBLE);
                clock.setEnabled(true);
            }
        });

        timer = new Timer();

        randomIdImages = new int[4];
        chooseRandomImages(myImageList, randomIdImages);

        for(ImageView image : imageViews) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer();

                    //First image selected
                    if(imageId1 == -1){
                        if(fail) {
                            fail = false;
                            frameAnimation.start();
                        }
                        imageId1 = imageViews.indexOf(image);
                    }
                    //Second image selected
                    else {
                        imageId2 = imageViews.indexOf(image);

                        if(imageId1.equals(imageId2)) {
                            Toast.makeText(MainActivity.this, R.string.choose_different, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Check hit
                            if (randomIdImages[imageId1] == randomIdImages[imageId2]) {
                                imageViews.get(imageId1).startAnimation(hitAnimation);
                                imageViews.get(imageId2).startAnimation(hitAnimation);
                                myImageList.remove((Integer) randomIdImages[imageId1]);
                                hitCounter++;
                                if(hitCounter < 3) {
                                    Toast.makeText(MainActivity.this, getString(R.string.normal_hit)
                                            + hitCounter, Toast.LENGTH_SHORT).show();
                                }
                                if(hitCounter == 3) {
                                    Toast.makeText(MainActivity.this, getString(R.string.great_hit)
                                            + hitCounter, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Fail
                            else {
                                fail = true;
                                for(ImageView image : imageViews) {
                                    image.startAnimation(failAnimation);
                                }
                                listResources(myImageList);
                                Toast.makeText(MainActivity.this,
                                        hitCounter > 0 ? R.string.great_fail : R.string.normal_fail,
                                        Toast.LENGTH_SHORT).show();
                                hitCounter = 0;
                            }
                        }
                    }
                    setImages();
                }
            });
        }
    }

    //This method set normal clock rotation and start clock by frames animation.
    private void startTimer() {
        if(!start) {

            frameAnimation.start();
            start = true;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!fail) {
                        clock.setRotation(clock.getRotation() + 5);
                    } else {
                        frameAnimation.stop();
                    }
                }
            };
            timer.schedule(task, 0, 40);
        }
    }

    //This method add png images from drawables to the list referenced by an Integer object.
    private void listResources(ArrayList<Integer> list) {
        if(list.size() != 0) {
            list.clear();
        }

        list.add(R.drawable.monster_0);
        list.add(R.drawable.monster_1);
        list.add(R.drawable.monster_2);
        list.add(R.drawable.monster_3);
        list.add(R.drawable.monster_4);
        list.add(R.drawable.monster_5);
        list.add(R.drawable.monster_6);
        list.add(R.drawable.monster_7);
    }

    //This method reset images to generic from drawables folder and enable the events.
    private void initImages(ArrayList<ImageView> imageList) {
        for(ImageView image : imageList) {
            image.setImageResource(R.drawable.generic);
            if(fail) {
                image.startAnimation(failChangeAnimation);
            } else {
                if (imageList.indexOf(image) == imageId1
                        || imageList.indexOf(image) == imageId2) {
                    image.startAnimation(hitChangeAnimation);
                } else {
                    //Prevent last running animation
                    image.startAnimation(new Animation() {
                        @Override
                        public void setDuration(long durationMillis) {
                            super.setDuration(0);
                        }
                    });
                }
            }
        }
    }

    //This method disables events on images.
    private void disableImages(ArrayList<ImageView> imageList) {
        for(ImageView image : imageList) {
            image.setEnabled(false);
        }
    }

    //This method enables events on images.
    private void enableImages(ArrayList<ImageView> imageList) {
        for(ImageView image : imageList) {
            image.setEnabled(true);
        }
    }

    /*
    This method get referenced images as Integer randomly from the original list
    and next shuffle it in the second parameter array.
     */
    private void chooseRandomImages(ArrayList<Integer> source, int[] list) {
        int repeatedImageId = source.get(getRandomNumber(source.size()-1, 0));
        list[0] = repeatedImageId;
        list[1] = repeatedImageId;

        for(int i = 2; i < list.length; i++) {
            boolean imageExist;
            do{
               imageExist = false;
               list[i] = source.get(getRandomNumber(source.size()-1, 0));

               for(int j = 1; j < i; j++) {
                  if(list[j] == list[i]) {
                      imageExist = true;
                      break;
                  }
               }
            } while(imageExist);
        }

        shuffleArray(list);
    }

    //This method is a generic random generator.
    public int getRandomNumber(int max, int min) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    //This method mess up an array by changing indexes between its members.
    private void shuffleArray(int[] array) {
        for(int i = 0; i < array.length - 1; i++) {
            int random = (int)(Math.random() * (array.length - i));
            int aux = array[array.length - 1 - i];
            array[array.length - 1 - i] = array[random];
            array[random] = aux;
        }
    }

    //This method change the resource generic to the referenced resource list index.
    private void setImages() {
        if(imageId2 == -1) {
            imageViews.get(imageId1).setImageResource(randomIdImages[imageId1]);
        }
        else {
            imageViews.get(imageId2).setImageResource(randomIdImages[imageId2]);
        }
    }

    private void endOfGame() {
            Toast.makeText(this, R.string.end_game, Toast.LENGTH_LONG).show();
            disableImages(imageViews);
            timer.cancel();
    }
}