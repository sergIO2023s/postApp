package com.example.postapp.domain.interfaces

interface  IResponse<T> {
   fun success(gg: T): ISuccess<T>
   fun error(gg: String): IError<T>
}

interface ISuccess<K>: IResponse<K> {
   fun data(): K
}
interface IError<K>: IResponse<K> {
   fun msg(): String
}