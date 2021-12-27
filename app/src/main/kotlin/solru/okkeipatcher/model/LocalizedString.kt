/**
 * Taken from Sesame library.
 * https://github.com/aartikov/Sesame/tree/master/sesame-localized-string
 *
 * Copyright (c) 2021 Artur Artikov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package solru.okkeipatcher.model

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import java.io.Serializable

/**
 * String with locale-dependent value.
 */
interface LocalizedString : Serializable {

	/**
	 * Resolves string value for a given [context].
	 */
	fun resolve(context: Context): CharSequence

	companion object {

		/**
		 * Creates an empty [LocalizedString].
		 */
		fun empty() = EmptyString

		/**
		 * Creates [LocalizedString] with a hardcoded value.
		 */
		fun raw(value: CharSequence): LocalizedString = when {
			value.isEmpty() -> EmptyString
			else -> RawString(value)
		}

		/**
		 * Creates [LocalizedString] represented by Android resource string with optional arguments. Arguments can be [LocalizedString]s as well.
		 */
		fun resource(@StringRes resourceId: Int, vararg args: Any) = ResourceString(resourceId, args.toList())

		/**
		 * Creates [LocalizedString] represented by plural Android resource with optional arguments. Arguments can be [LocalizedString]s as well.
		 */
		fun quantity(@PluralsRes resourceId: Int, quantity: Int, vararg args: Any) =
			QuantityResourceString(resourceId, quantity, args.toList())
	}
}

/**
 * Concatenates two [LocalizedString]s.
 */
operator fun LocalizedString.plus(other: LocalizedString): LocalizedString {
	return when {
		this is CompoundString && other is CompoundString -> CompoundString(this.parts + other.parts)
		this is CompoundString -> CompoundString(this.parts + other)
		other is CompoundString -> CompoundString(listOf(this) + other.parts)
		else -> CompoundString(listOf(this, other))
	}
}

object EmptyString : LocalizedString {
	override fun resolve(context: Context): CharSequence {
		return ""
	}
}

data class RawString(val value: CharSequence) : LocalizedString {
	override fun resolve(context: Context): CharSequence {
		return value
	}
}

data class ResourceString(
	@StringRes val resourceId: Int,
	val args: List<Any>
) : LocalizedString {
	override fun resolve(context: Context): CharSequence {
		return context.getString(resourceId, *getArgValues(context, args))
	}
}

data class QuantityResourceString(
	@PluralsRes val resourceId: Int,
	val quantity: Int,
	val args: List<Any>
) : LocalizedString {
	override fun resolve(context: Context): CharSequence {
		return context.resources.getQuantityString(resourceId, quantity, *getArgValues(context, args))
	}
}

data class CompoundString(val parts: List<LocalizedString>) : LocalizedString {

	override fun resolve(context: Context): CharSequence {
		return parts.joinToString(separator = "") { it.resolve(context) }
	}
}

private fun getArgValues(context: Context, args: List<Any>): Array<Any> {
	return args.map {
		if (it is LocalizedString) {
			it.resolve(context)
		} else {
			it
		}
	}.toTypedArray()
}