import android.os.Parcel
import android.os.Parcelable

data class Producto(val id: Int, val nombre: String, val precio: Int, val variantes: List<Variante>?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.createTypedArrayList(Variante)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeInt(precio)
        parcel.writeTypedList(variantes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Producto> {
        override fun createFromParcel(parcel: Parcel): Producto {
            return Producto(parcel)
        }

        override fun newArray(size: Int): Array<Producto?> {
            return arrayOfNulls(size)
        }
    }
}

data class Variante(val nombre: String, val precio: Int, var seleccionada: Boolean?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeInt(precio)
        parcel.writeValue(seleccionada)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Variante> {
        override fun createFromParcel(parcel: Parcel): Variante {
            return Variante(parcel)
        }

        override fun newArray(size: Int): Array<Variante?> {
            return arrayOfNulls(size)
        }
    }
}
