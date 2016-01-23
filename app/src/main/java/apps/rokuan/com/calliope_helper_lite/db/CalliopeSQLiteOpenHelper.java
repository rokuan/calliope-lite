package apps.rokuan.com.calliope_helper_lite.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rokuan.calliopecore.fr.parser.SpeechParser;
import com.rokuan.calliopecore.fr.parser.WordDatabase;
import com.rokuan.calliopecore.fr.sentence.Action;
import com.rokuan.calliopecore.fr.sentence.AdjectiveInfo;
import com.rokuan.calliopecore.fr.sentence.CharacterInfo;
import com.rokuan.calliopecore.fr.sentence.CityInfo;
import com.rokuan.calliopecore.fr.sentence.ColorInfo;
import com.rokuan.calliopecore.fr.sentence.CountryInfo;
import com.rokuan.calliopecore.fr.sentence.CustomMode;
import com.rokuan.calliopecore.fr.sentence.CustomObject;
import com.rokuan.calliopecore.fr.sentence.CustomPerson;
import com.rokuan.calliopecore.fr.sentence.CustomPlace;
import com.rokuan.calliopecore.fr.sentence.LanguageInfo;
import com.rokuan.calliopecore.fr.sentence.NameInfo;
import com.rokuan.calliopecore.fr.sentence.PlaceInfo;
import com.rokuan.calliopecore.fr.sentence.PlacePreposition;
import com.rokuan.calliopecore.fr.sentence.PurposePreposition;
import com.rokuan.calliopecore.fr.sentence.TimePreposition;
import com.rokuan.calliopecore.fr.sentence.TransportInfo;
import com.rokuan.calliopecore.fr.sentence.UnitInfo;
import com.rokuan.calliopecore.fr.sentence.Verb;
import com.rokuan.calliopecore.fr.sentence.VerbAction;
import com.rokuan.calliopecore.fr.sentence.VerbConjugation;
import com.rokuan.calliopecore.fr.sentence.WayPreposition;
import com.rokuan.calliopecore.fr.sentence.Word;
import com.rokuan.calliopecore.sentence.IAction;
import com.rokuan.calliopecore.sentence.IAdjectiveInfo;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;
import com.rokuan.calliopecore.sentence.structure.data.nominal.CharacterObject;
import com.rokuan.calliopecore.sentence.structure.data.nominal.UnitObject;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.place.PlaceObject;
import com.rokuan.calliopecore.sentence.structure.data.purpose.PurposeAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.time.TimeAdverbial;
import com.rokuan.calliopecore.sentence.structure.data.way.TransportObject;
import com.rokuan.calliopecore.sentence.structure.data.way.WayAdverbial;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class CalliopeSQLiteOpenHelper extends OrmLiteSqliteOpenHelper implements WordDatabase {
    public static final String DATA_SEPARATOR = ";";
    public static final String SPECIAL_ENCODING = "ISO-8859-1";

    private static final Class<?>[] COMMON_CLASSES = {
            Word.class,
            VerbConjugation.class,
            PlacePreposition.class,
            TimePreposition.class,
            WayPreposition.class,
            PurposePreposition.class,
            NameInfo.class,
            AdjectiveInfo.class,
            LanguageInfo.class,
            ColorInfo.class,
            CityInfo.class,
            CountryInfo.class,
            TransportInfo.class,
            UnitInfo.class,
            CharacterInfo.class,
            PlaceInfo.class
    };
    private static final String[] COMMON_COLUMN_NAMES = {
            // Ordre different pour des raisons de performance
            Word.WORD_FIELD_NAME,
            VerbConjugation.VALUE_FIELD_NAME,
            PlacePreposition.VALUE_FIELD_NAME,
            TimePreposition.VALUE_FIELD_NAME,
            WayPreposition.VALUE_FIELD_NAME,
            PurposePreposition.VALUE_FIELD_NAME,
            NameInfo.VALUE_FIELD_NAME,
            AdjectiveInfo.VALUE_FIELD_NAME,
            LanguageInfo.LANGUAGE_FIELD_NAME,
            ColorInfo.COLOR_FIELD_NAME,
            CityInfo.CITY_FIELD_NAME,
            CountryInfo.COUNTRY_FIELD_NAME,
            TransportInfo.TRANSPORT_FIELD_NAME,
            UnitInfo.UNIT_FIELD_NAME,
            CharacterInfo.CHARACTER_FIELD_NAME,
            PlaceInfo.PLACE_FIELD_NAME
    };

    private static final String DB_NAME = "calliope_helper_lite";
    private static final int DB_VERSION = 1;
    private Context context;
    private EventBus defaultBus = EventBus.getDefault();
    private SpeechParser parser;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        parser = new SpeechParser(this);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            // TODO: table des prenoms ?
            TableUtils.createTable(connectionSource, Word.class);

            TableUtils.createTable(connectionSource, NameInfo.class);
            TableUtils.createTable(connectionSource, AdjectiveInfo.class);
            TableUtils.createTable(connectionSource, CityInfo.class);
            TableUtils.createTable(connectionSource, CountryInfo.class);
            TableUtils.createTable(connectionSource, LanguageInfo.class);
            TableUtils.createTable(connectionSource, ColorInfo.class);
            TableUtils.createTable(connectionSource, TransportInfo.class);
            TableUtils.createTable(connectionSource, UnitInfo.class);
            TableUtils.createTable(connectionSource, CharacterInfo.class);
            TableUtils.createTable(connectionSource, PlaceInfo.class);

            TableUtils.createTable(connectionSource, Verb.class);
            TableUtils.createTable(connectionSource, Action.class);
            TableUtils.createTable(connectionSource, VerbAction.class);
            TableUtils.createTable(connectionSource, VerbConjugation.class);

            TableUtils.createTable(connectionSource, PlacePreposition.class);
            TableUtils.createTable(connectionSource, TimePreposition.class);
            TableUtils.createTable(connectionSource, WayPreposition.class);
            TableUtils.createTable(connectionSource, PurposePreposition.class);

            defaultBus.post(new DatabaseEvent("Mots"));
            loadWords(connectionSource);
            loadCommonNames(connectionSource);
            loadAdjectives(connectionSource);
            defaultBus.post(new DatabaseEvent("Pays"));
            loadCountries(connectionSource);
            defaultBus.post(new DatabaseEvent("Villes"));
            loadCities(connectionSource);
            defaultBus.post(new DatabaseEvent("Langues"));
            loadLanguages(connectionSource);
            defaultBus.post(new DatabaseEvent("Couleurs"));
            loadColors(connectionSource);
            defaultBus.post(new DatabaseEvent("Moyens de locomotion"));
            loadTransports(connectionSource);
            defaultBus.post(new DatabaseEvent("Unités"));
            loadUnits(connectionSource);
            defaultBus.post(new DatabaseEvent("Types de lieux"));
            loadPlaces(connectionSource);
            defaultBus.post(new DatabaseEvent("Types de personnes"));
            loadCharacters(connectionSource);
            defaultBus.post(new DatabaseEvent("Verbes"));
            loadActions(connectionSource);
            loadVerbs(connectionSource);
            loadVerbActions(connectionSource);
            loadConjugations(connectionSource);
            defaultBus.post(new DatabaseEvent("Autres"));
            loadPlacePrepositions(connectionSource);
            loadTimePrepositions(connectionSource);
            loadWayPrepositions(connectionSource);
            loadPurposePrepositions(connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // TODO:
    }

    private static <T> void loadData(ConnectionSource connectionSource, Class<T> dataClass, Context context, String assetName, DataAdapter<T> adapter) throws IOException, SQLException {
        AssetManager assets = context.getAssets();
        InputStream in = assets.open(assetName);
        Scanner sc = new Scanner(in);
        Dao<T, ?> dao = DaoManager.createDao(connectionSource, dataClass);

        while(sc.hasNextLine()){
            String line = sc.nextLine();
            dao.create(adapter.transform(line));
        }

        in.close();
        sc.close();
    }

    private void loadWords(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<Word> wordAdapter = new DataAdapter<Word>() {
            @Override
            public Word transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                String[] types = fields[1].split(",");
                Set<Word.WordType> wordTypes = new HashSet<Word.WordType>();

                for(String ty: types){
                    wordTypes.add(Word.WordType.valueOf(ty));
                }

                return new Word(fields[0], wordTypes);
            }
        };

        loadData(connectionSource, Word.class, context, "words.txt", wordAdapter);
    }

    private void loadCommonNames(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<NameInfo> nameAdapter = new DataAdapter<NameInfo>() {
            @Override
            public NameInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new NameInfo(fields[0], fields[1]);
            }
        };

        loadData(connectionSource, NameInfo.class, context, "common_names.txt", nameAdapter);
    }

    private void loadAdjectives(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<AdjectiveInfo> adjectiveAdapter = new DataAdapter<AdjectiveInfo>() {
            @Override
            public AdjectiveInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                IAdjectiveInfo.AdjectiveValue value = "_".equals(fields[1]) ? IAdjectiveInfo.AdjectiveValue.UNDEFINED : IAdjectiveInfo.AdjectiveValue.valueOf(fields[1]);
                return new AdjectiveInfo(fields[0], value);
            }
        };

        loadData(connectionSource, AdjectiveInfo.class, context, "adjectives.txt", adjectiveAdapter);
    }

    private void loadCities(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<CityInfo> cityAdapter = new DataAdapter<CityInfo>() {
            @Override
            public CityInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new CityInfo(fields[2], Double.parseDouble(fields[0]), Double.parseDouble(fields[1]));
            }
        };

        loadData(connectionSource, CityInfo.class, context, "cities.txt", cityAdapter);
    }

    private void loadCountries(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<CountryInfo> countryAdapter = new DataAdapter<CountryInfo>() {
            @Override
            public CountryInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new CountryInfo(fields[4], fields[2]);
            }
        };

        loadData(connectionSource, CountryInfo.class, context, "countries.txt", countryAdapter);
    }

    private void loadLanguages(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<LanguageInfo> languageAdapter = new DataAdapter<LanguageInfo>() {
            @Override
            public LanguageInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new LanguageInfo(fields[0], fields[1]);
            }
        };

        loadData(connectionSource, LanguageInfo.class, context, "languages.txt", languageAdapter);
    }

    private void loadColors(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<ColorInfo> colorAdapter = new DataAdapter<ColorInfo>() {
            @Override
            public ColorInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new ColorInfo(fields[0], fields[1]);
            }
        };

        loadData(connectionSource, ColorInfo.class, context, "colors.txt", colorAdapter);
    }

    private void loadTransports(ConnectionSource connectionSource) throws SQLException, IOException {
        DataAdapter<TransportInfo> transportAdapter = new DataAdapter<TransportInfo>() {
            @Override
            public TransportInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new TransportInfo(fields[0], TransportObject.TransportType.valueOf(fields[1]));
            }
        };

        loadData(connectionSource, TransportInfo.class, context, "transports.txt", transportAdapter);
    }

    private void loadUnits(ConnectionSource connectionSource) throws SQLException, IOException {
        DataAdapter<UnitInfo> unitAdapter = new DataAdapter<UnitInfo>() {
            @Override
            public UnitInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new UnitInfo(fields[0], UnitObject.UnitType.valueOf(fields[1]));
            }
        };

        loadData(connectionSource, UnitInfo.class, context, "units.txt", unitAdapter);
    }

    private void loadCharacters(ConnectionSource connectionSource) throws SQLException, IOException {
        DataAdapter<CharacterInfo> characterAdapter = new DataAdapter<CharacterInfo>() {
            @Override
            public CharacterInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new CharacterInfo(fields[0], CharacterObject.CharacterType.valueOf(fields[1]));
            }
        };

        loadData(connectionSource, CharacterInfo.class, context, "characters.txt", characterAdapter);
    }

    private void loadPlaces(ConnectionSource connectionSource) throws SQLException, IOException {
        DataAdapter<PlaceInfo> placeAdapter = new DataAdapter<PlaceInfo>() {
            @Override
            public PlaceInfo transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                return new PlaceInfo(fields[0], PlaceObject.PlaceCategory.valueOf(fields[1]));
            }
        };

        loadData(connectionSource, PlaceInfo.class, context, "places.txt", placeAdapter);
    }

    private void loadActions(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<Action> actionAdapter = new DataAdapter<Action>() {
            @Override
            public Action transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                IAction.ActionType action = IAction.ActionType.valueOf(fields[0]);

                if(fields.length > 1){
                    return new Action(action, fields[1]);
                } else {
                    return new Action(action);
                }
            }
        };

        loadData(connectionSource, Action.class, context, "actions.txt", actionAdapter);
    }

    private void loadVerbs(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<Verb> verbAdapter = new DataAdapter<Verb>() {
            @Override
            public Verb transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                boolean auxiliary = (Integer.parseInt(fields[3]) != 0);

                return new Verb(fields[0], auxiliary);
            }
        };

        loadData(connectionSource, Verb.class, context, "verbs.txt", verbAdapter);
    }

    private void loadVerbActions(ConnectionSource connectionSource) throws IOException, SQLException {
        final Dao<Verb, String> verbDao = DaoManager.createDao(connectionSource, Verb.class);
        final Dao<Action, String> actionDao = DaoManager.createDao(connectionSource, Action.class);

        DataAdapter<VerbAction> verbActionAdapter = new DataAdapter<VerbAction>() {
            @Override
            public VerbAction transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                Verb v = null;
                Action a = null;

                try {
                    v = verbDao.queryForId(fields[0]);
                } catch (SQLException e) {

                }
                try {
                    //a = actionDao.queryForId(fields[1]);
                    PreparedQuery<Action> query = actionDao.queryBuilder()
                            .where()
                            .eq(Action.ACTION_FIELD_NAME, IAction.ActionType.valueOf(fields[1]))
                            .prepare();
                    a = actionDao.queryForFirst(query);
                } catch (SQLException e) {

                }

                return new VerbAction(v, a);
            }
        };

        loadData(connectionSource, VerbAction.class, context, "verb_actions.txt", verbActionAdapter);
    }

    private void loadConjugations(ConnectionSource connectionSource) throws IOException, SQLException {
        final Dao<Verb, String> verbDao = DaoManager.createDao(connectionSource, Verb.class);

        DataAdapter<VerbConjugation> conjugationAdapter = new DataAdapter<VerbConjugation>() {
            @Override
            public VerbConjugation transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                Verb.Pronoun pronoun = null;
                Verb verb = null;

                try {
                    verb = verbDao.queryForId(fields[0]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try{
                    pronoun = Verb.Pronoun.values()[Integer.parseInt(fields[4])];
                }catch(Exception e){

                }

                return new VerbConjugation(Verb.ConjugationTense.valueOf(fields[3]),
                        IAction.Form.valueOf(fields[2]),
                        pronoun,
                        fields[1],
                        verb
                );
            }
        };

        loadData(connectionSource, VerbConjugation.class, context, "conjugations.txt", conjugationAdapter);
    }

    private void loadPlacePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<PlacePreposition> placePrepositionAdapter = new DataAdapter<PlacePreposition>() {
            @Override
            public PlacePreposition transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                String[] types = fields[2].split(",");
                Set<PlaceAdverbial.PlaceType> prepTypes = new HashSet<PlaceAdverbial.PlaceType>();

                for(String ty: types){
                    prepTypes.add(PlaceAdverbial.PlaceType.valueOf(ty));
                }

                return new PlacePreposition(fields[0], PlaceAdverbial.PlaceContext.valueOf(fields[1]), prepTypes);
            }
        };

        loadData(connectionSource, PlacePreposition.class, context, "place_prepositions.txt", placePrepositionAdapter);
    }

    private void loadTimePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<TimePreposition> timePrepositionAdapter = new DataAdapter<TimePreposition>() {
            @Override
            public TimePreposition transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                String[] types = fields[2].split(",");
                Set<TimeAdverbial.TimeType> prepTypes = new HashSet<TimeAdverbial.TimeType>();

                for(String ty: types){
                    prepTypes.add(TimeAdverbial.TimeType.valueOf(ty));
                }

                return new TimePreposition(fields[0], TimeAdverbial.TimeContext.valueOf(fields[1]), prepTypes);
            }
        };

        loadData(connectionSource, TimePreposition.class, context, "time_prepositions.txt", timePrepositionAdapter);
    }

    private void loadWayPrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<WayPreposition> wayPrepositionAdapter = new DataAdapter<WayPreposition>() {
            @Override
            public WayPreposition transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                String[] types = fields[2].split(",");
                Set<WayAdverbial.WayType> prepTypes = new HashSet<WayAdverbial.WayType>();

                for(String ty: types){
                    prepTypes.add(WayAdverbial.WayType.valueOf(ty));
                }

                return new WayPreposition(fields[0], WayAdverbial.WayContext.valueOf(fields[1]), prepTypes);
            }
        };

        loadData(connectionSource, WayPreposition.class, context, "way_prepositions.txt", wayPrepositionAdapter);
    }

    private void loadPurposePrepositions(ConnectionSource connectionSource) throws IOException, SQLException {
        DataAdapter<PurposePreposition> purposePrepositionAdapter = new DataAdapter<PurposePreposition>() {
            @Override
            public PurposePreposition transform(String s) {
                String[] fields = s.split(DATA_SEPARATOR);
                String[] types = fields[2].split(",");
                Set<PurposeAdverbial.PurposeType> prepTypes = new HashSet<PurposeAdverbial.PurposeType>();

                for(String ty: types){
                    prepTypes.add(PurposeAdverbial.PurposeType.valueOf(ty));
                }

                return new PurposePreposition(fields[0], PurposeAdverbial.PurposeContext.valueOf(fields[1]), prepTypes);
            }
        };

        loadData(connectionSource, PurposePreposition.class, context, "purpose_prepositions.txt", purposePrepositionAdapter);
    }

    public InterpretationObject parseText(String text){
        return parser.parseText(text);
    }

    @Override
    public boolean wordStartsWith(String q){
        ConnectionSource connectionSource = this.getConnectionSource();
        boolean exists = false;

        for(int i=0; i<COMMON_CLASSES.length; i++){
            try {
                Dao<?, ?> dao = DaoManager.createDao(connectionSource, COMMON_CLASSES[i]);
                QueryBuilder builder = dao.queryBuilder();

                long count = builder.where()
                        .eq(COMMON_COLUMN_NAMES[i], q)
                        .or()
                        .between(COMMON_COLUMN_NAMES[i], q + " ", q + "ý").countOf();
                exists = (count > 0);

                if(exists){
                    break;
                }
            } catch (SQLException e) {

            }
        }

        return exists;
    }

    @Override
    public Word findWord(String q) {
        return queryFirst(this, Word.class, Word.WORD_FIELD_NAME, q);
    }

    @Override
    public NameInfo findNameInfo(String q) {
        return queryFirst(this, NameInfo.class, NameInfo.VALUE_FIELD_NAME, q);
    }

    @Override
    public AdjectiveInfo findAdjectiveInfo(String q) {
        return queryFirst(this, AdjectiveInfo.class, AdjectiveInfo.VALUE_FIELD_NAME, q);
    }

    @Override
    public LanguageInfo findLanguageInfo(String q){
        return queryFirst(this, LanguageInfo.class, LanguageInfo.LANGUAGE_FIELD_NAME, q);
    }

    @Override
    public ColorInfo findColorInfo(String q){
        return queryFirst(this, ColorInfo.class, ColorInfo.COLOR_FIELD_NAME, q);
    }

    @Override
    public CityInfo findCityInfo(String q){
        return queryFirst(this, CityInfo.class, CityInfo.CITY_FIELD_NAME, q);
    }

    @Override
    public CountryInfo findCountryInfo(String q){
        return queryFirst(this, CountryInfo.class, CountryInfo.COUNTRY_FIELD_NAME, q);
    }

    @Override
    public TransportInfo findTransportInfo(String q) {
        return queryFirst(this, TransportInfo.class, TransportInfo.TRANSPORT_FIELD_NAME, q);
    }

    @Override
    public UnitInfo findUnitInfo(String q) {
        return queryFirst(this, UnitInfo.class, UnitInfo.UNIT_FIELD_NAME, q);
    }

    @Override
    public CharacterInfo findCharacterInfo(String q) {
        return queryFirst(this, CharacterInfo.class, CharacterInfo.CHARACTER_FIELD_NAME, q);
    }

    @Override
    public PlaceInfo findPlaceInfo(String q) {
        return queryFirst(this, PlaceInfo.class, PlaceInfo.PLACE_FIELD_NAME, q);
    }

    @Override
    public CustomObject findCustomObject(String q){
        return null;
    }

    @Override
    public CustomPlace findCustomPlace(String q){
        return null;
    }

    @Override
    public CustomMode findCustomMode(String q) {
        return null;
    }

    @Override
    public CustomPerson findCustomPerson(String q) {
        return null;
    }

    @Override
    public PlacePreposition findPlacePreposition(String q) {
        return queryFirst(this, PlacePreposition.class, PlacePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public TimePreposition findTimePreposition(String q) {
        return queryFirst(this, TimePreposition.class, TimePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public WayPreposition findWayPreposition(String q) {
        return queryFirst(this, WayPreposition.class, WayPreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public PurposePreposition findPurposePreposition(String q) {
        return queryFirst(this, PurposePreposition.class, PurposePreposition.VALUE_FIELD_NAME, q);
    }

    @Override
    public VerbConjugation findConjugation(String q){
        return queryFirst(this, VerbConjugation.class, VerbConjugation.VALUE_FIELD_NAME, q);
    }

    private static <T> T queryFirst(OrmLiteSqliteOpenHelper helper, Class<T> objectClass, String columnName, String queryString){
        ConnectionSource connectionSource = helper.getConnectionSource();

        try {
            Dao<T, String> dao = DaoManager.createDao(connectionSource, objectClass);
            QueryBuilder builder = dao.queryBuilder();
            PreparedQuery<T> preparedQuery = builder.where().eq(columnName, queryString.replaceAll("'", "\\'")).prepare();
            T result = dao.queryForFirst(preparedQuery);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
