package ru.solrudev.okkeipatcher.data.core

import android.content.Context
import ru.solrudev.okkeipatcher.domain.core.*

/**
 * Resolves string value for a given [context].
 */
fun LocalizedString.resolve(context: Context): CharSequence = when (this) {
	is EmptyString -> ""
	is RawString -> value
	is ResourceString -> context.getString(resourceId, *getArgValues(context, args))
	is CompoundString -> parts.joinToString(separator = "") { it.resolve(context) }
	is QuantityResourceString -> context.resources.getQuantityString(resourceId, quantity, *getArgValues(context, args))
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