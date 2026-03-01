package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IBookRepository
import org.delcom.repositories.BookRepository
import org.delcom.services.PlantService
import org.delcom.services.BookService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {

    // ======================
    // PLANT
    // ======================
    single<IPlantRepository> {
        PlantRepository()
    }

    single {
        PlantService(get())
    }

    // ======================
    // BOOK (WAJIB ADA)
    // ======================
    single<IBookRepository> {
        BookRepository()
    }

    single {
        BookService(get())
    }

    // ======================
    // PROFILE
    // ======================
    single {
        ProfileService()
    }
}