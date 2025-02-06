package com.nminh123.xidach;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class Screen implements com.badlogic.gdx.Screen
{
    Stage stage;
    OrthographicCamera cam;
    Viewport viewport;
    Image background;
    TextureRegion backgroundImage;

    //Button
    Image hitBtn, stayBtn, replayBtn;
    Texture buttonImage, pressedButtonImage;

    ///Initialize core game attribute
    ArrayList<Card> deck;
    Random random = new Random(); //shuffle deck

    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;
    Texture hiddenCardImage;

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    boolean isStayBtnClicked = false;

    public Screen()
    {
        cam = new OrthographicCamera();
        viewport = new ExtendViewport(Consts.cardWidth,Consts.boardHeight, cam);
        stage = new Stage(viewport);
        buttonImage = new Texture(Gdx.files.internal("button-over.png"));
        pressedButtonImage = new Texture(Gdx.files.internal("button-pressed-over.png"));
        hiddenCardImage = new Texture(Gdx.files.internal(Consts.BACKCARDIMG));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show()
    {
        setUpBackground();
        setUpHitBtn();
        setUpStayBtn();
        setUpReplayBtn();

        startGame();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1);

        stage.act(delta);
        stage.draw();
    }

    void setUpBackground()
    {
        backgroundImage = new TextureRegion(new Texture(Gdx.files.internal("bj_table_color.jpg")));
        background = new Image(backgroundImage);
        background.setSize(900,600);
        stage.addActor(background);
    }

    void startGame()
    {
        builDeck();
        shuffleDeck();
        dealerHand();
        playerHand();
    }

    void builDeck()
    {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "S", "D", "H"};

        for(int type = 0; type < types.length; type++) {
            for(int value = 0; value < values.length; value++) {
                Card card = new Card(values[value], types[type]);
                deck.add(card);
            }
        }

        System.out.println("BUILD DECK:");
        System.out.println(deck);
    }

    void shuffleDeck()
    {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    void dealerHand()
    {
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1); //Trừ giá trị cuối trong arraylist
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        drawDealerCard();

        System.out.println("DEALER HIDDEN CARD: " + hiddenCard + "\t \t DEALDER HAND: " + dealerHand + "\t \t DEALER SUM: " + dealerSum + "\t \t DEALER ACE COUNT: " + dealerAceCount);
    }

    void drawDealerCard() {
        Image backcard = new Image(hiddenCardImage);
        backcard.setSize(Consts.cardWidth, Consts.cardHeight);
        backcard.setPosition(300, 420);

        stage.addActor(backcard);


        for (int i = 0; i < dealerHand.size(); i++) {
            Card card = dealerHand.get(i);
            Texture cardTexture = new Texture(Gdx.files.internal(card.getImagePath()));
            Image image = new Image(cardTexture);
            image.setSize(Consts.cardWidth, Consts.cardHeight);

            // Set card position (adjust X to avoid overlap)
            image.setPosition(180, 420);
            image.debug();

            stage.addActor(image);
            System.out.println("Texture: " + cardTexture);
        }
    }

    void playerHand()
    {
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;
        Card card;

        for (int i = 0; i < 2; i++)
        {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        drawPlayerCard();

        System.out.println("PLAYER HAND: " + playerHand + "\t \t PLAYER SUM: " + playerSum + "\t \t PLAYER ACE COUNT: " + playerAceCount);
    }

    void drawPlayerCard()
    {
        Image[] images = new Image[2];

        for (int i = 0; i < playerHand.size() - 1; i++)
        {
            Card card = playerHand.get(i);
            Texture cardTexture = new Texture(Gdx.files.internal(card.getImagePath()));

            images[0] = new Image(cardTexture);
            images[0].setSize(Consts.cardWidth, Consts.cardHeight);
            images[0].setPosition(180, 130);

            stage.addActor(images[0]);
        }

        for (int i = 1; i < playerHand.size(); i++)
        {
            Card card = playerHand.get(i);
            Texture cardTexture = new Texture(Gdx.files.internal(card.getImagePath()));

            images[1] = new Image(cardTexture);
            images[1].setSize(Consts.cardWidth, Consts.cardHeight);
            images[1].setPosition(300, 130);

            stage.addActor(images[1]);
        }
    }

    void hitCard()
    {
        Card card = deck.remove(deck.size() - 1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;

        playerHand.add(card);
    }

    void drawHitCard()
    {
        for(int i = playerHand.size() - 1; i >= playerHand.size() - 1; i--)
        {
            Card card = playerHand.get(i);
            Texture cardTexture = new Texture(Gdx.files.internal(card.getImagePath()));
            Image image = new Image(cardTexture);
            image.setSize(Consts.cardWidth, Consts.cardHeight);
            image.setPosition(300 + 120,130);

            stage.addActor(image);
        }
    }

    void setUpHitBtn()
    {
        hitBtn = new Image(buttonImage);
        hitBtn.setPosition(50,0);
        hitBtn.setSize(150, 50);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.BLACK;

        TextField field = new TextField("", style);
        field.setText("Hit");
        field.setSize(130, 30);
        field.setPosition(60,10);
        field.setAlignment(Align.center);
        field.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hitBtn.setDrawable(new Image(pressedButtonImage).getDrawable());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                hitBtn.setDrawable(new Image(buttonImage).getDrawable());
                hitCard();
                drawHitCard();
                System.out.println("AFTER HIT: PLAYER HAND: " + playerHand + "\t \t PLAYER SUM: " + playerSum + "\t \t PLAYER ACE COUNT: " + playerAceCount);
            }
        });

        stage.addActor(hitBtn);
        stage.addActor(field);
    }

    void setUpStayBtn()
    {
        stayBtn = new Image(buttonImage);
        stayBtn.setSize(150, 50);
        stayBtn.setPosition(235, 0);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.BLACK;

        TextField field = new TextField("", style);
        field.setText("Stay");
        field.setSize(130, 30);
        field.setPosition(245,10);
        field.setAlignment(Align.center);
        field.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stayBtn.setDrawable(new Image(pressedButtonImage).getDrawable());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                stayBtn.setDrawable(new Image(buttonImage).getDrawable());
                isStayBtnClicked = true; // nhớ set lại trạng thái khi hoàn thành màn chơi.
            }
        });

        stage.addActor(stayBtn);
        stage.addActor(field);
    }

    void setUpReplayBtn()
    {
        replayBtn = new Image(buttonImage);
        replayBtn.setSize(150, 50);
        replayBtn.setPosition(420, 0);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.BLACK;

        TextField field = new TextField("", style);
        field.setText("Again");
        field.setSize(130, 30);
        field.setPosition(430,10);
        field.setAlignment(Align.center);
        field.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                replayBtn.setDrawable(new Image(pressedButtonImage).getDrawable());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                replayBtn.setDrawable(new Image(buttonImage).getDrawable());
                ((Game)Gdx.app.getApplicationListener()).setScreen(new Screen());
            }
        });

        stage.addActor(replayBtn);
        stage.addActor(field);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose()
    {
        stage.dispose();
        buttonImage.dispose();
        pressedButtonImage.dispose();
    }
}