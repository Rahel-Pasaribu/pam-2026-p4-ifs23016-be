CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL

    CREATE TABLE IF NOT EXISTS books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    cover_path VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    main_character VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );
);