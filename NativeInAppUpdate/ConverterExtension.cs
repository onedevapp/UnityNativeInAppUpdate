
using System;

namespace OneDevApp.InAppUpdate
{
    /// <summary>
    /// Helper class for converting downloading size to user readable format
    /// </summary>
    public static class ConverterExtension
    {
        private const long OneKb = 1024;
        private const long OneMb = OneKb * 1024;
        private const long OneGb = OneMb * 1024;
        private const long OneTb = OneGb * 1024;

        public static string ToPrettySize(this int value, bool toShowUnit, int decimalPlaces = 0)
        {
            return ((long)value).ToPrettySize(toShowUnit, decimalPlaces);
        }

        public static string ToPrettySize(this long value, bool toShowUnit, int decimalPlaces = 0)
        {
            var asTb = Math.Round((double)value / OneTb, decimalPlaces);
            var asGb = Math.Round((double)value / OneGb, decimalPlaces);
            var asMb = Math.Round((double)value / OneMb, decimalPlaces);
            var asKb = Math.Round((double)value / OneKb, decimalPlaces);
            string chosenValue = asTb > 1 ? (toShowUnit ? string.Format("{0}Tb", asTb) : asTb.ToString())
                : asGb > 1 ? (toShowUnit ? string.Format("{0}Gb", asGb) : asGb.ToString())
                : asMb > 1 ? (toShowUnit ? string.Format("{0}Mb", asMb) : asMb.ToString())
                : asKb > 1 ? (toShowUnit ? string.Format("{0}Kb", asKb) : asKb.ToString())
                : (toShowUnit ? string.Format("{0}B", Math.Round((double)value, decimalPlaces)) : Math.Round((double)value, decimalPlaces).ToString());
            return chosenValue;
        }
    }

}