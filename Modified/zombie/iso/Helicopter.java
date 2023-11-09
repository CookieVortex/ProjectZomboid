package zombie.iso;

import fmod.FMODSoundBuffer;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.javafmod;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManager;
import zombie.core.raknet.VoiceManagerData;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;
import zombie.network.FakeClientManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

import zombie.network.chat.ChatServer;
import zombie.util.list.PZArrayList;
import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import static zombie.network.GameServer.sendServerCommand;


public class Helicopter {
    private static float MAX_BOTHER_SECONDS = 60.0F;
    private static float MAX_UNSEEN_SECONDS = 15.0F;
    private static int RADIUS_HOVER = 50;
    private static int RADIUS_SEARCH = 100;
    protected Helicopter.State state;
    public IsoGameCharacter target;
    protected float timeSinceChopperSawPlayer;
    protected float hoverTime;
    protected float searchTime;
    public float x;
    public float y;
    protected float targetX;
    protected float targetY;
    protected Vector2 move = new Vector2();
    protected boolean bActive;
    protected static long inst;
    protected static FMOD_STUDIO_EVENT_DESCRIPTION event;
    protected boolean bSoundStarted;
    protected float volume;
    protected float occlusion;

    public long nextTimeOfMessage;
    int chance;

    /*== Поиски: где угодно ==*/
    public String[] messageSearching = new String []
            {
            "Что за шутки? Тут даже муха не выживет, а они где-то здесь?",
            "Если кто-то выжил, то что он здесь делает? Я сомневаюсь, что кто-то умудрился остаться в живых.",
            "Искать выживших? В это время? Сомневаюсь, что кто-то здесь смог выжить.",
            "Если бы они были здесь, я бы их уже давно нашел. Но они, наверное, все умерли.",
            "Думаете, кто-то хотел бы остаться живым в мире, где царят зомби? Сомневаюсь, что такие люди еще остались.",
            "Выжившие? Да они, скорее всего, уже погибли от зомби или других выживших.",
            "Находить выживших — это как искать иголку в стоге сена. Вероятность крайне мала.",
            "Зачем искать то, чего уже нет? Выжившие — это просто легенда.",
            "Выжившие? Уверен, их уже все поглотила эта апокалиптическая ночь.",
            "Может, я здесь потратил свое время зря. Выживших тут точно нет.",

            "Если тут есть кто-нибудь живой, дайте знак!",
            "Если кто-то слышит меня, помогите! Я пришел спасать!",
            "Если выжившие где-то рядом, давайте откликнитесь!",
            "Если кто-то услышал мой призыв, покажите себя! Я здесь, чтобы помочь!",
            "Если кто-то еще живой, ответьте мне! Я здесь, чтобы спасти вас!",
            "Если кто-то может меня слышать, сигнализируйте! Я здесь, чтобы вывезти из этой жуткой ямы!",
            "Если кто-нибудь выжил в этом аду, присоединяйтесь! Я найду вас и доставлю в безопасное место!",
            "Если кто-то где-то здесь, я пришел, чтобы спасти! Откликнитесь, кто-нибудь!",
            "Если тут кто-то еще живой, скажите мне! Я сошел с ума ждать спасения так долго!",
            "Если остался хоть один выживший, докажите это! Я ищу вас!",

            "Уф, искать безуспешно так долго, уже утомило.",
            "Безрезультатные поиски так и не дают мне покоя. Я устал.",
            "Мое терпение на исходе, нигде не нахожу выживших.",
            "Так много потраченного времени, а ничего не нашел... Сил уже почти нет.",
            "Бесконечный поиск и пустота... Кажется, я уже потерял всякую надежду.",
            "Продолжать искать выживших становится все сложнее. Я и сам себе начинаю казаться последним оставшимся.",
            "Уставший и безрезультатный поиск начинает отнимать последние силы.",
            "Искать выживших становится все тяжелее и бессмысленнее. Но я не могу прекратить.",
            "Усталость охватывает меня, но я не могу остановиться. Я обязан найти выживших.",
            "Этот мир полон пустоты... Мое тело устало, но мне всё равно приходится искать.",

            "Если кто-то здесь жив, не дерзайте выходить. Я решу, что с вами делать.",
            "Если выжившие тут находятся, то лучше мне их выбросить из этой жалкой иллюзии безопасности.",
            "Выживших я ищу, но это не значит, что они мне нужны для помощи. Не переоценивайте свою ценность.",
            "Если кто-то остался, это лучше всего для меня. Так я не буду скучать после этого поиска.",
            "Если есть выжившие, выходите, пока я не изменил свои планы и не решил убрать вас.",
            "Не ждите милосердия от меня, если я найду вас. Я не иду сюда на спасение.",
            "Ваше выживание будет зависеть от моей настроенности в данный момент. Вы предупреждены.",
            "Если кто-то выжил, он станет моей пешкой. Иначе, мы решим это иначе.",
            "Найденные выжившие станут либо слугами, либо пищей для зомби. Не важно, что они выберут.",
            "Если я вас найду, вам придется подчиниться или исчезнуть. Это выбор будет однозначным.",

            "Кто-нибудь живой тут? Или все ушли на обед с зомби?",
            "Ищу выживших, но все встречаются только зомби. И это не самое интересное свидание.",
            "Думаете, кто-то решит остаться в живых, живя среди зомби? Мда, это отличный выбор жилья.",
            "А зачем сотрудничать с другими, если можно устроить свидание с мертвецами?",
            "Безуспешный поиск выживших — это все равно что совать руку в капкан.",
            "Эта игра в !поймай выжившего! начинает становиться скучной. Может, найду что-нибудь более интересное.",
            "Кто-то слышит мои призывы? Возможно, ответят только зомби, а не те, кого я ищу.",
            "Желанный приз уже долго не идет, наверное, никто не хочет быть спасенным от всех этих ужасов.",
            "Ищу выживших. А если найду? Буду просить подпись на автографе.",
            "Если я найду выжившего, смогу потребовать награду за выполнение такой нелегкой задачи?",

            "Может, я найду выживших, но буду делать все, чтобы они не мешали моим планам.",
            "Чем больше выживших я найду, тем меньше их будет. Это закон жестокой апокалипсисной природы.",
            "Ищу выживших, чтобы сделать им последний отдых. Зомби настолько однообразны.",
            "Если кто-то выжил, я могу лишить его этого статуса. Убивая.",
            "Вам не удастся ускользнуть от моего внимания, если вы выживший. Мое внимание — это смерть.",
            "Намеренно ищу выживших, чтобы стереть их надежды одним движением моего оружия.",
            "Если найду выжившего, сделаю ему последнюю услугу — отправлю Вечным Сном раньше зомби.",
            "Выживший для меня — только игрушка для удовлетворения моих потребностей.",
            "Прячьтесь, выжившие, пока я не найду и не прикончу своей личной зомби-армадой.",
            "В жестоком мире зомби, выжившим требуется лишь капля моего вдохновения — и они уйдут по той же дороге, что и все остальные."
            };


    /*== Открытая местность ==*/
    protected String[] messageHoveringOnRoad = new String []
            {
                    "О, посмотри-ка, кто здесь! Привет, малыш, ты просто отличная мишень...",
                    "Ты действительно думал, что сможешь убежать от нашего взгляда? Ты смешной.",
                    "Ай-яй-яй, что мы здесь имеем? Маленький выживший, такой слабенький и беспомощный...",
                    "Ты выглядишь так уверенным, как будто исполнишь все свои мечты... Жди ОГНЕСТРЕЛЬНОГО подарка от нас!",
                    "Ох, милая попытка добраться до безопасности, но мы всё равно предложим свои услуги по прерыванию твоей жалкой жизни!",
                    "Правда ли ты думаешь, что сможешь справиться с нами и нашим вертолетом?",
                    "О, такой маленький и беззащитный. Так и хочется избавиться от тебя",
                    "Скажу по секрету, ты тут весьма никчемный. Неужели думаешь, что ты быстрее пули?",
                    "Ты, малыш, и твое маленькое сопротивление - это просто мясо для нашего пулемета!",
                    "Мы раздавим тебя, как маленькое насекомое не жалея ни патронов, ни усилий!",
                    "Попытайся удрать, если сможешь, зомби уже требуют тебя на обед.",
                    "Привлеките зомби в его направлении. Пусть они покажут ему шоу, ха-ха!",
                    "Эй, я вижу толпу ходячих, которые идут в твою сторону, жди гостей, дохляк!",
                    "Слышишь эти звуки? Это скоро будут выстрелы, набирающие скорость.",
                    "Готовь бинты, они тебе сейчас пригодятся.",
                    "Пусть выстрелы будут твоим гимном, потому что скоро они станут последним звуком, который ты услышишь.",
                    "Ты думал, что хорошо выглядишь? Сейчас тебя украсит пара дырок от пуль!",
                    "О, смотрю, ты забыл, как звучат выстрелы. Но сейчас напомню, хе-хе.",
                    "Пока орда бессмысленно бродит по округе, я направлю их в твою сторону, выживший."
            };

    protected String[] messageHoveringOnWindow = new String [] {

    };

    protected String[] messageHoveringOnCar = new String [] {

    };

    protected String[] messageHoveringOnFastVehicle = new String [] {

    };




    public void pickRandomTarget() {
        ArrayList var1;
        if (GameServer.bServer) {
            var1 = GameServer.getPlayers();
        } else {
            if (GameClient.bClient) {
                throw new IllegalStateException("can't call this on the client");
            }

            var1 = new ArrayList();

            for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
                IsoPlayer var3 = IsoPlayer.players[var2];
                if (var3 != null && var3.isAlive()) {
                    var1.add(var3);
                }
            }
        }

        if (var1.isEmpty()) {
            this.bActive = false;
            this.target = null;
        } else {
            this.setTarget((IsoGameCharacter)var1.get(Rand.Next(var1.size())));
        }
    }

    public void setTarget(IsoGameCharacter var1) {
        this.target = var1;
        this.x = this.target.x + 1000.0F;
        this.y = this.target.y + 1000.0F;
        this.targetX = this.target.x;
        this.targetY = this.target.y;
        this.move.x = this.targetX - this.x;
        this.move.y = this.targetY - this.y;
        this.move.normalize();
        this.move.setLength(0.5F);
        this.state = Helicopter.State.Arriving;
        this.bActive = true;
        this.nextTimeOfMessage = GameTime.getServerTime();
        DebugLog.log("chopper: activated");
    }

    protected void changeState(Helicopter.State var1) {
        DebugLog.log("chopper: state " + this.state + " -> " + var1);
        this.state = var1;
    }

    public void update() {
        if (this.bActive) {
            if (!GameClient.bClient) {
                float var1 = 1.0F;
                if (GameServer.bServer) {
                    if (!GameServer.Players.contains(this.target)) {
                        this.target = null;
                    }
                } else {
                    var1 = GameTime.getInstance().getTrueMultiplier();
                }

                switch (this.state) {
                    case Arriving:
                        if (this.target != null && !this.target.isDead()) {
                            if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 4.0F) {
                                this.changeState(State.Hovering);
                                this.hoverTime = 0.0F;
                                this.searchTime = 0.0F;
                                this.timeSinceChopperSawPlayer = 0.0F;
                            } else {
                                this.targetX = this.target.x;
                                this.targetY = this.target.y;
                                this.move.x = this.targetX - this.x;
                                this.move.y = this.targetY - this.y;
                                this.move.normalize();
                                this.move.setLength(0.75F);
                            }
                        } else {
                            this.changeState(State.Leaving);
                        }
                        break;
                    case Hovering:
                        if (this.target != null && !this.target.isDead()) {
                            this.hoverTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * var1;
                            if (this.hoverTime + this.searchTime > MAX_BOTHER_SECONDS) {
                                this.changeState(State.Leaving);
                            } else {
                                if (!this.isTargetVisible()) {
                                    this.timeSinceChopperSawPlayer += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * var1;
                                    if (this.timeSinceChopperSawPlayer > MAX_UNSEEN_SECONDS) {
                                        this.changeState(State.Searching);
                                        break;
                                    }
                                }

                                if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 1.0F) {
                                    this.targetX = this.target.x + (float) (Rand.Next(RADIUS_HOVER * 2) - RADIUS_HOVER);
                                    this.targetY = this.target.y + (float) (Rand.Next(RADIUS_HOVER * 2) - RADIUS_HOVER);
                                    this.move.x = this.targetX - this.x;
                                    this.move.y = this.targetY - this.y;
                                    this.move.normalize();
                                    this.move.setLength(0.5F);
                                    IsoGameCharacter player = this.target;
                                    IsoPlayer play = this.target.current.getPlayer();


                                    DebugLog.log("<<< Start SOUND MOTHERFUCKY 1 ");

                                    RakVoice.SendFrame(FakeClientManager.getConnectedGUID(), FakeClientManager.getOnlineID(), VoiceManager.FMODReceiveBuffer.buf(), VoiceManager.FMODReceiveBuffer.get_size());


                                    PZArrayList <IsoObject> pzObjects = play.getCurrentSquare().getObjects();
                                    for(int i = 0; i < pzObjects.size(); ++i) {
                                        if (pzObjects.get(i) instanceof IsoWindow) {
                                            IsoWindow win = (IsoWindow) pzObjects.get(i);
                                            GameServer.smashWindow(win,1);
                                            UdpConnection udpPlayer = GameServer.getConnectionByPlayerOnlineID(play.OnlineID);
                                            ChatServer.getInstance().sendMessageToServerChat(udpPlayer,"Песдюк у окна!");
                                        }
                                    }
                                    //play.getEmitter().playSound()


                                }
                            }
                        } else {
                            this.changeState(State.Leaving);
                        }
                        break;
                    case Searching:
                        sendMessageFromHelicopter(messageSearching);


                        if (this.target != null && !this.target.isDead()) {
                            this.searchTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * var1;
                            if (this.hoverTime + this.searchTime > MAX_BOTHER_SECONDS) {
                                this.changeState(State.Leaving);
                            } else if (this.isTargetVisible()) {
                                this.timeSinceChopperSawPlayer = 0.0F;
                                this.changeState(State.Hovering);
                            } else if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 1.0F) {
                                this.targetX = this.target.x + (float) (Rand.Next(RADIUS_SEARCH * 2) - RADIUS_SEARCH);
                                this.targetY = this.target.y + (float) (Rand.Next(RADIUS_SEARCH * 2) - RADIUS_SEARCH);
                                this.move.x = this.targetX - this.x;
                                this.move.y = this.targetY - this.y;
                                this.move.normalize();
                                this.move.setLength(0.5F);
                            }
                        } else {
                            this.state = State.Leaving;
                        }
                        break;
                    case Leaving:
                        boolean var2 = false;
                        if (GameServer.bServer) {
                            ArrayList var7 = GameServer.getPlayers();

                            for (int var9 = 0; var9 < var7.size(); ++var9) {
                                IsoPlayer var5 = (IsoPlayer) var7.get(var9);
                                if (IsoUtils.DistanceToSquared(this.x, this.y, var5.getX(), var5.getY()) < 1000000.0F) {
                                    var2 = true;
                                    break;
                                }
                            }
                        } else {
                            for (int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
                                IsoPlayer var4 = IsoPlayer.players[var3];
                                if (var4 != null && IsoUtils.DistanceToSquared(this.x, this.y, var4.getX(), var4.getY()) < 1000000.0F) {
                                    var2 = true;
                                    break;
                                }
                            }
                        }

                        if (!var2) {
                            this.deactivate();
                            return;
                        }
                }

                if (Rand.Next(Rand.AdjustForFramerate(300)) == 0) {
                    WorldSoundManager.instance.addSound((Object) null, (int) this.x, (int) this.y, 0, 500, 500);
                }

                float var6 = this.move.x * (GameTime.getInstance().getMultiplier() / 1.6F);
                float var8 = this.move.y * (GameTime.getInstance().getMultiplier() / 1.6F);
                if (this.state != State.Leaving && IsoUtils.DistanceToSquared(this.x + var6, this.y + var8, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY)) {
                    this.x = this.targetX;
                    this.y = this.targetY;
                } else {
                    this.x += var6;
                    this.y += var8;
                }

                if (GameServer.bServer) {
                    GameServer.sendHelicopter(this.x, this.y, this.bActive);
                }

            } this.updateSound();
        }
    }

    private void sendDamage(IsoPlayer player) {
        IsoPlayer iPlayer = player;
        KahluaTable khTable = LuaManager.platform.newTable();
        khTable.rawset("bodyPartIndex", KahluaUtil.toDouble(Rand.Next(14)));
        khTable.rawset("action", KahluaUtil.rawTostring("bullet"));
        khTable.rawset("id", KahluaUtil.toDouble(iPlayer.getOnlineID()));
        sendServerCommand(iPlayer, "ISHealthPanel", "onHealthCheat", khTable);
    }

    protected void updateSound() {
        if (!GameServer.bServer) {
            if (!Core.SoundDisabled) {
                if (FMODManager.instance.getNumListeners() != 0) {
                    GameSound var1 = GameSounds.getSound("Helicopter");
                    if (var1 != null && !var1.clips.isEmpty()) {
                        if (inst == 0L) {
                            GameSoundClip var2 = var1.getRandomClip();
                            event = var2.eventDescription;
                            if (event != null) {
                                javafmod.FMOD_Studio_LoadEventSampleData(event.address);
                                inst = javafmod.FMOD_Studio_System_CreateEventInstance(event.address);
                            }
                        }

                        if (inst != 0L) {
                            float var5 = SoundManager.instance.getSoundVolume();
                            var5 *= var1.getUserVolume();
                            if (var5 != this.volume) {
                                javafmod.FMOD_Studio_EventInstance_SetVolume(inst, var5);
                                this.volume = var5;
                            }

                            javafmod.FMOD_Studio_EventInstance3D(inst, this.x, this.y, 200.0F);
                            float var3 = 0.0F;
                            if (IsoPlayer.numPlayers == 1) {
                                IsoGridSquare var4 = IsoPlayer.getInstance().getCurrentSquare();
                                if (var4 != null && !var4.Is(IsoFlagType.exterior)) {
                                    var3 = 1.0F;
                                }
                            }

                            if (this.occlusion != var3) {
                                this.occlusion = var3;
                                javafmod.FMOD_Studio_EventInstance_SetParameterByName(inst, "Occlusion", this.occlusion);
                            }

                            if (!this.bSoundStarted) {
                                javafmod.FMOD_Studio_StartEvent(inst);
                                this.bSoundStarted = true;
                            }
                        }

                    }
                }
            }
        }
    }

    protected boolean isTargetVisible() {
        if (this.target != null && !this.target.isDead()) {
            IsoGridSquare var1 = this.target.getCurrentSquare();
            if (var1 == null) {
                return false;
            } else if (!var1.getProperties().Is(IsoFlagType.exterior)) {
                return false;
            } else {
                IsoMetaGrid.Zone var2 = var1.getZone();
                if (var2 == null) {
                    return true;
                } else {
                    return !"Forest".equals(var2.getType()) && !"DeepForest".equals(var2.getType());
                }
            }
        } else {
            return false;
        }
    }

    public void deactivate() {
        if (this.bActive) {
            this.bActive = false;
            if (this.bSoundStarted) {
                javafmod.FMOD_Studio_EventInstance_Stop(inst, false);
                this.bSoundStarted = false;
            }

            if (GameServer.bServer) {
                GameServer.sendHelicopter(this.x, this.y, this.bActive);
            }

            DebugLog.log("chopper: deactivated");
        }

    }

    public boolean isActive() {
        return this.bActive;
    }

    public void clientSync(float var1, float var2, boolean var3) {
        if (GameClient.bClient) {
            this.x = var1;
            this.y = var2;
            if (!var3) {
                this.deactivate();
            }
            this.bActive = var3;
        }
    }

    /*== Если игрок на открытой местности ==*/
    private void damagePlayerOnArea(IsoPlayer play) {
        if (nextTimeOfMessage < GameTime.getServerTime()) {
            sendDamage(play);
            UdpConnection udpPlayer = GameServer.getConnectionByPlayerOnlineID(play.OnlineID);
            nextTimeOfMessage += 100000000000L; //100000000000L = 37 сек |
            DebugLog.log("<<<See ya now! >:D");
            GameServer.PlayWorldSoundServer("MetaAssaultRifle1", false, play.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
        }
    }

    /*== Если игрок в здании ==*/
    private void damagePlayerUnderRoof(IsoPlayer play) {
        if (play.getSquare().haveRoof) {
            UdpConnection udpPlayer = GameServer.getConnectionByPlayerOnlineID(play.OnlineID);
            ChatServer.getInstance().sendMessageToServerChat(udpPlayer,"Песдюк в здании!");
            DebugLog.log("<<< Building - " + play.getBuilding()) ;
        }
    }

    /*== Если игрок в машине ==*/
    private void damagePlayerInVehicle(IsoPlayer play) {
        boolean isExist = false;
        int chance;
        if (play.getVehicle() != null) {
            if (play.getVehicle().getCurrentSpeedKmHour() >= 50) {
                UdpConnection udpPlayer = GameServer.getConnectionByPlayerOnlineID(play.OnlineID);
                ChatServer.getInstance().sendMessageToServerChat(udpPlayer, messageHoveringOnFastVehicle[Rand.Next(messageHoveringOnFastVehicle.length)]);
            } else {
                String partsOfCar[] = new String[]{"WindowFrontRight", "WindowFrontLeft", "Windshield", "HeadlightFrontRight", "HeadlightFrontLeft", "HeadlightRearRight", "HeadlightRearLeft", "TireFrontLeft", "TireFrontRight", "TireRearLeft", "TireRearRight"};
                int partDamage;

                partDamage = Rand.Next(5,65);
                String randomPartName = "";

                while (!isExist) {
                    randomPartName = partsOfCar[Rand.Next(partsOfCar.length)];
                    if (randomPartName != "" & randomPartName != null) {
                        int partCondition = play.getVehicle().getPartById(randomPartName).getCondition();

                        if (partCondition < partDamage & (randomPartName.equals("Windshield") | randomPartName.equals("WindowFrontRight") | randomPartName.equals("WindowFrontLeft"))) {
                            isExist = true;
                            play.getVehicle().getPartById(randomPartName).damage(partDamage);
                            sendDamage(play);
                            GameServer.PlayWorldSoundServer("MetaAssaultRifle1", false, play.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                        } else {
                            play.getVehicle().getPartById(randomPartName).damage(partDamage);
                            GameServer.PlayWorldSoundServer("MetaAssaultRifle1", false, play.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                        }
                    }
                }
            }
        }
    }

    private void sendMessageFromHelicopter (String[] messages) {
        for(int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(i);

            float playerX = GameServer.getPlayerFromConnection(udpConnection, 0).getX();
            float playerY = GameServer.getPlayerFromConnection(udpConnection, 0).getY();

            if (IsoUtils.DistanceToSquared(this.x, this.y, playerX, playerY) <= 100.0f) {
                ChatServer.getInstance().sendMessageToServerChat(udpConnection,messages[Rand.Next(messages.length)]);
                DebugLog.log("Пишу игроку, до него " + String.valueOf(IsoUtils.DistanceToSquared(this.x, this.y, playerX, playerY) <= 100.0f));
            }
        }
    }

    private int getChance(IsoPlayer play) {
        if (play.isOutside()) {
            this.chance = 70;
            if (play.isInTrees()) {
                this.chance = 10;
            }
            if (play.isSprinting()) {
                this.chance = 20;
            }
            if (play.isRunning()) {
                this.chance = 30;
            }
            if (play.isSeatedInVehicle()) {
               if (play.getVehicle().getCurrentSpeedKmHour() >= 50) {
                   this.chance = 0;
               }
               else {
                   this.chance = 90;
               }

            }
        }

        return this.chance;
    }


    private static enum State {
        Arriving,
        Hovering,
        Searching,
        Leaving;

        // $FF: synthetic method
        private static Helicopter.State[] $values() {
            return new Helicopter.State[]{Arriving, Hovering, Searching, Leaving};
        }
    }




        public static void read() {
            try {
                // Получаем доступ к микрофону
                AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                // Создаем аудио поток для записи звука с микрофона
                AudioInputStream audioInputStream = new AudioInputStream(line);

                // Записываем звуковой файл с микрофона в файл
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("recorded.wav"));

                // Загружаем звуковой файл
                AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(new File("sound.wav"));

                // Получаем доступ к аудио устройству для воспроизведения
                DataLine.Info info2 = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line2 = (SourceDataLine) AudioSystem.getLine(info2);
                line2.open(format);
                line2.start();

                // Воспроизводим звуковой файл
                byte[] buffer = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = audioInputStream2.read(buffer)) != -1) {
                    line2.write(buffer, 0, bytesRead);
                }

                // Закрываем линии и освобождаем ресурсы
                line.stop();
                line.close();
                line2.drain();
                line2.stop();
                line2.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
