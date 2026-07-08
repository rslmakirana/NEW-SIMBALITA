package com.example.simbalita.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.simbalita.model.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "simbalita.db";
    private static final int DATABASE_VERSION = 5;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CHILDREN = "children";
    public static final String TABLE_EXAMINATIONS = "examinations";
    public static final String TABLE_SCHEDULES = "schedules";
    public static final String TABLE_ARTICLES = "articles";

    // User columns
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";
    public static final String COL_USER_NIK = "nik";
    public static final String COL_USER_ADDRESS = "address";
    public static final String COL_USER_USERNAME = "username";

    // Child columns
    public static final String COL_CHILD_ID = "id";
    public static final String COL_CHILD_NAME = "name";
    public static final String COL_CHILD_BIRTH_DATE = "birth_date";
    public static final String COL_CHILD_GENDER = "gender";
    public static final String COL_CHILD_BIRTH_WEIGHT = "birth_weight";
    public static final String COL_CHILD_BIRTH_HEIGHT = "birth_height";
    public static final String COL_CHILD_MOTHER_ID = "mother_id";

    // Examination columns
    public static final String COL_EXAM_ID = "id";
    public static final String COL_EXAM_CHILD_ID = "child_id";
    public static final String COL_EXAM_DATE = "date";
    public static final String COL_EXAM_WEIGHT = "weight";
    public static final String COL_EXAM_HEIGHT = "height";
    public static final String COL_EXAM_STATUS = "status";

    // Schedule columns
    public static final String COL_SCH_ID = "id";
    public static final String COL_SCH_DATE = "date";
    public static final String COL_SCH_TIME = "time";
    public static final String COL_SCH_TITLE = "title";
    public static final String COL_SCH_LOCATION = "location";

    // Article columns
    public static final String COL_ART_ID = "id";
    public static final String COL_ART_TITLE = "title";
    public static final String COL_ART_CATEGORY = "category";
    public static final String COL_ART_CONTENT = "content";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ROLE + " TEXT, " +
                COL_USER_NIK + " TEXT, " +
                COL_USER_ADDRESS + " TEXT, " +
                COL_USER_USERNAME + " TEXT UNIQUE)";

        String createChildrenTable = "CREATE TABLE " + TABLE_CHILDREN + " (" +
                COL_CHILD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CHILD_NAME + " TEXT, " +
                COL_CHILD_BIRTH_DATE + " TEXT, " +
                COL_CHILD_GENDER + " TEXT, " +
                COL_CHILD_BIRTH_WEIGHT + " REAL, " +
                COL_CHILD_BIRTH_HEIGHT + " REAL, " +
                COL_CHILD_MOTHER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_CHILD_MOTHER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)";

        String createExaminationsTable = "CREATE TABLE " + TABLE_EXAMINATIONS + " (" +
                COL_EXAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXAM_CHILD_ID + " INTEGER, " +
                COL_EXAM_DATE + " TEXT, " +
                COL_EXAM_WEIGHT + " REAL, " +
                COL_EXAM_HEIGHT + " REAL, " +
                COL_EXAM_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COL_EXAM_CHILD_ID + ") REFERENCES " + TABLE_CHILDREN + "(" + COL_CHILD_ID + ") ON DELETE CASCADE)";

        String createSchedulesTable = "CREATE TABLE " + TABLE_SCHEDULES + " (" +
                COL_SCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SCH_DATE + " TEXT, " +
                COL_SCH_TIME + " TEXT, " +
                COL_SCH_TITLE + " TEXT, " +
                COL_SCH_LOCATION + " TEXT)";

        String createArticlesTable = "CREATE TABLE " + TABLE_ARTICLES + " (" +
                COL_ART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ART_TITLE + " TEXT, " +
                COL_ART_CATEGORY + " TEXT, " +
                COL_ART_CONTENT + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createChildrenTable);
        db.execSQL(createExaminationsTable);
        db.execSQL(createSchedulesTable);
        db.execSQL(createArticlesTable);

        // Preseed Admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_USER_NAME, "Kader Posyandu");
        adminValues.put(COL_USER_PHONE, "081234567890");
        adminValues.put(COL_USER_PASSWORD, "admin12345");
        adminValues.put(COL_USER_ROLE, "ADMIN");
        adminValues.put(COL_USER_NIK, "0000000000000000");
        adminValues.put(COL_USER_ADDRESS, "Posyandu Melati 1");
        adminValues.put(COL_USER_USERNAME, "admin");
        db.insert(TABLE_USERS, null, adminValues);

        // Preseed Schedules
        seedSchedules(db);

        // Preseed Articles
        seedArticles(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMINATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedSchedules(SQLiteDatabase db) {
        String[][] listJadwal = {
                {"2026-07-15", "08.00 WIB", "Posyandu Melati 1", "Jl. Melati No. 10"},
                {"2026-08-12", "08.00 WIB", "Posyandu Melati 1", "Jl. Melati No. 10"},
                {"2026-09-16", "08.00 WIB", "Posyandu Melati 1", "Jl. Melati No. 10"}
        };

        for (String[] sch : listJadwal) {
            ContentValues values = new ContentValues();
            values.put(COL_SCH_DATE, sch[0]);
            values.put(COL_SCH_TIME, sch[1]);
            values.put(COL_SCH_TITLE, sch[2]);
            values.put(COL_SCH_LOCATION, sch[3]);
            db.insert(TABLE_SCHEDULES, null, values);
        }
    }

    private void seedArticles(SQLiteDatabase db) {
        String[][] listArtikel = {
                {
                        "MPASI Sehat untuk Anak 6-12 Bulan",
                        "Nutrisi",
                        "Makanan Pendamping ASI (MPASI) yang baik dimulai dari usia 6 bulan. Pastikan makanan memiliki tekstur yang tepat sesuai usia bayi. Mulailah dari bubur saring lembut, lalu perlahan naik ke makanan lunak, dan makanan keluarga di usia 12 bulan. Nutrisi MPASI harus mengandung gizi seimbang yang terdiri dari karbohidrat, protein hewani, protein nabati, dan lemak sehat sebagai sumber energi utama anak."
                },
                {
                        "Cegah Stunting Sejak Dini",
                        "Pertumbuhan",
                        "Stunting adalah kondisi gagal tumbuh pada anak akibat kekurangan gizi kronis dalam 1000 Hari Pertama Kehidupan (HPK). Cara pencegahan stunting antara lain: memberikan ASI eksklusif sampai 6 bulan, dilanjutkan dengan MPASI berkualitas, menjaga kebersihan lingkungan dan sanitasi, memantau tumbuh kembang anak di Posyandu secara rutin, serta memberikan imunisasi dasar lengkap agar anak terhindar dari penyakit infeksi."
                },
                {
                        "Imunisasi Lengkap untuk Anak",
                        "Kesehatan",
                        "Imunisasi melindungi anak dari berbagai penyakit berbahaya seperti TBC, hepatitis B, difteri, pertusis, tetanus, polio, campak, dan rubela. Pastikan anak mendapatkan imunisasi dasar lengkap sebelum berusia 1 tahun dan imunisasi booster sesuai rekomendasi Ikatan Dokter Anak Indonesia (IDAI). Rutin berkonsultasi dengan petugas kesehatan di posyandu mengenai jadwal imunisasi anak Anda."
                }
        };

        for (String[] art : listArtikel) {
            ContentValues values = new ContentValues();
            values.put(COL_ART_TITLE, art[0]);
            values.put(COL_ART_CATEGORY, art[1]);
            values.put(COL_ART_CONTENT, art[2]);
            db.insert(TABLE_ARTICLES, null, values);
        }
    }

    // --- User Methods ---
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_ROLE, user.getRole());
        values.put(COL_USER_NIK, user.getNik());
        values.put(COL_USER_ADDRESS, user.getAddress());
        values.put(COL_USER_USERNAME, user.getUsername());
        return db.insert(TABLE_USERS, null, values);
    }

    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_USERNAME + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NIK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME))
            );
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public List<User> getMothers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_ROLE + "=?", new String[]{"IBU"}, null, null, COL_USER_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NIK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NIK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME))
            );
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean resetPassword(String username, String nik, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, values,
                COL_USER_USERNAME + "=? AND " + COL_USER_NIK + "=?",
                new String[]{username, nik});
        return rows > 0;
    }

    // --- Child Methods ---
    public long addChild(Child child) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CHILD_NAME, child.getName());
        values.put(COL_CHILD_BIRTH_DATE, child.getBirthDate());
        values.put(COL_CHILD_GENDER, child.getGender());
        values.put(COL_CHILD_BIRTH_WEIGHT, child.getBirthWeight());
        values.put(COL_CHILD_BIRTH_HEIGHT, child.getBirthHeight());
        values.put(COL_CHILD_MOTHER_ID, child.getMotherId());
        return db.insert(TABLE_CHILDREN, null, values);
    }

    public boolean updateChild(Child child) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CHILD_NAME, child.getName());
        values.put(COL_CHILD_BIRTH_DATE, child.getBirthDate());
        values.put(COL_CHILD_GENDER, child.getGender());
        values.put(COL_CHILD_BIRTH_WEIGHT, child.getBirthWeight());
        values.put(COL_CHILD_BIRTH_HEIGHT, child.getBirthHeight());
        values.put(COL_CHILD_MOTHER_ID, child.getMotherId());
        return db.update(TABLE_CHILDREN, values, COL_CHILD_ID + "=?", new String[]{String.valueOf(child.getId())}) > 0;
    }

    public List<Child> getChildrenByMother(int motherId) {
        List<Child> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHILDREN, null, COL_CHILD_MOTHER_ID + "=?", new String[]{String.valueOf(motherId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Child(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_GENDER)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_HEIGHT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_MOTHER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Child> getAllChildren() {
        List<Child> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHILDREN, null, null, null, null, null, COL_CHILD_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(new Child(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_GENDER)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_HEIGHT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_MOTHER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Child> searchChildren(String query) {
        List<Child> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Join with users to search by mother's name as well
        String rawQuery = "SELECT c.* FROM " + TABLE_CHILDREN + " c LEFT JOIN " + TABLE_USERS + " u ON c." + COL_CHILD_MOTHER_ID + " = u." + COL_USER_ID +
                " WHERE c." + COL_CHILD_NAME + " LIKE ? OR u." + COL_USER_NAME + " LIKE ?";
        Cursor cursor = db.rawQuery(rawQuery, new String[]{"%" + query + "%", "%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Child(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_GENDER)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_HEIGHT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_MOTHER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Child getChildById(int childId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHILDREN, null, COL_CHILD_ID + "=?", new String[]{String.valueOf(childId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Child child = new Child(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CHILD_GENDER)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_WEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CHILD_BIRTH_HEIGHT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_CHILD_MOTHER_ID))
            );
            cursor.close();
            return child;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void deleteChild(int childId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHILDREN, COL_CHILD_ID + "=?", new String[]{String.valueOf(childId)});
    }

    // --- Examination Methods ---
    public long addExamination(Examination exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXAM_CHILD_ID, exam.getChildId());
        values.put(COL_EXAM_DATE, exam.getDate());
        values.put(COL_EXAM_WEIGHT, exam.getWeight());
        values.put(COL_EXAM_HEIGHT, exam.getHeight());
        
        // Calculate status automatically
        String calculatedStatus = calculateNutritionalStatus(exam.getChildId(), exam.getDate(), exam.getWeight(), exam.getHeight());
        values.put(COL_EXAM_STATUS, calculatedStatus);
        
        return db.insert(TABLE_EXAMINATIONS, null, values);
    }

    public List<Examination> getExaminationsByChild(int childId) {
        List<Examination> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXAMINATIONS, null, COL_EXAM_CHILD_ID + "=?", new String[]{String.valueOf(childId)}, null, null, COL_EXAM_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                list.add(new Examination(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_CHILD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_HEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Examination getLatestExamination(int childId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXAMINATIONS, null, COL_EXAM_CHILD_ID + "=?", new String[]{String.valueOf(childId)}, null, null, COL_EXAM_DATE + " DESC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            Examination exam = new Examination(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_CHILD_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_DATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_WEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_HEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_STATUS))
            );
            cursor.close();
            return exam;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int getCheckupsCountByDate(String dateString) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXAMINATIONS + " WHERE " + COL_EXAM_DATE + " = ?", new String[]{dateString});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<Examination> getExaminationsByPeriod(String period) {
        // period is in "YYYY-MM" format
        List<Examination> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXAMINATIONS + " WHERE " + COL_EXAM_DATE + " LIKE ?", new String[]{period + "%"});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Examination(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXAM_CHILD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXAM_HEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXAM_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- Status Auto-Calculation ---
    public String calculateNutritionalStatus(int childId, String examDate, double weight, double height) {
        Child child = getChildById(childId);
        if (child == null) return "Normal";

        int ageInMonths = calculateAgeInMonths(child.getBirthDate(), examDate);

        // Simple WHO standard approximation:
        // Let's create a rough lookup table:
        if (ageInMonths < 6) {
            if (weight < 4.5) return "Kurang";
            if (weight > 8.5) return "Lebih";
            if (height < 55) return "Stunting";
        } else if (ageInMonths < 12) {
            if (weight < 6.5) return "Kurang";
            if (weight > 11.5) return "Lebih";
            if (height < 66) return "Stunting";
        } else if (ageInMonths < 18) {
            if (weight < 8.0) return "Kurang";
            if (weight > 13.5) return "Lebih";
            if (height < 74) return "Stunting";
        } else if (ageInMonths < 24) {
            if (weight < 9.2) return "Kurang";
            if (weight > 15.0) return "Lebih";
            if (height < 80) return "Stunting";
        } else if (ageInMonths < 36) {
            if (weight < 11.0) return "Kurang";
            if (weight > 17.5) return "Lebih";
            if (height < 86) return "Stunting";
        } else if (ageInMonths < 48) {
            if (weight < 12.5) return "Kurang";
            if (weight > 20.0) return "Lebih";
            if (height < 93) return "Stunting";
        } else {
            if (weight < 14.0) return "Kurang";
            if (weight > 23.0) return "Lebih";
            if (height < 100) return "Stunting";
        }
        return "Normal";
    }

    public static int calculateAgeInMonths(String birthDateStr, String currentDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date birthDate = sdf.parse(birthDateStr);
            Date currentDate = sdf.parse(currentDateStr);
            
            Calendar birth = Calendar.getInstance();
            birth.setTime(birthDate);
            Calendar current = Calendar.getInstance();
            current.setTime(currentDate);

            int diffYear = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + current.get(Calendar.MONTH) - birth.get(Calendar.MONTH);

            return Math.max(0, diffMonth);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String formatAge(int months) {
        int years = months / 12;
        int remainingMonths = months % 12;
        if (years > 0) {
            return years + " Tahun " + remainingMonths + " Bulan";
        } else {
            return months + " Bulan";
        }
    }

    // --- Schedule Methods ---
    public long addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCH_DATE, schedule.getDate());
        values.put(COL_SCH_TIME, schedule.getTime());
        values.put(COL_SCH_TITLE, schedule.getTitle());
        values.put(COL_SCH_LOCATION, schedule.getLocation());
        return db.insert(TABLE_SCHEDULES, null, values);
    }

    public boolean updateSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCH_DATE, schedule.getDate());
        values.put(COL_SCH_TIME, schedule.getTime());
        values.put(COL_SCH_TITLE, schedule.getTitle());
        values.put(COL_SCH_LOCATION, schedule.getLocation());
        return db.update(TABLE_SCHEDULES, values, COL_SCH_ID + "=?", new String[]{String.valueOf(schedule.getId())}) > 0;
    }

    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COL_SCH_ID + "=?", new String[]{String.valueOf(scheduleId)});
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHEDULES, null, null, null, null, null, COL_SCH_DATE + " ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(new Schedule(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCH_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_LOCATION))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Schedule getUpcomingSchedule() {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());
        
        Cursor cursor = db.query(TABLE_SCHEDULES, null, COL_SCH_DATE + ">=?", new String[]{today}, null, null, COL_SCH_DATE + " ASC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            Schedule schedule = new Schedule(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCH_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_LOCATION))
            );
            cursor.close();
            return schedule;
        }
        // Fallback to first schedule in DB if no upcoming schedule is found
        if (cursor != null) cursor.close();
        cursor = db.query(TABLE_SCHEDULES, null, null, null, null, null, COL_SCH_DATE + " DESC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            Schedule schedule = new Schedule(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCH_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCH_LOCATION))
            );
            cursor.close();
            return schedule;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // --- Article Methods ---
    public List<Article> getAllArticles() {
        List<Article> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ARTICLES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Article(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ART_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_CONTENT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Article getArticleById(int articleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ARTICLES, null, COL_ART_ID + "=?", new String[]{String.valueOf(articleId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Article article = new Article(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ART_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ART_CONTENT))
            );
            cursor.close();
            return article;
        }
        if (cursor != null) cursor.close();
        return null;
    }
}
