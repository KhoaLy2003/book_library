export function calculateDaysBetweenDates(
  startDateStr: string,
  endDateStr: string
): number {
  // Convert string dates to Date objects
  const startDate = new Date(startDateStr);
  const endDate = new Date(endDateStr);

  // Check if dates are valid
  if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
    throw new Error('Invalid date format');
  }

  // Calculate the time difference in milliseconds
  const timeDiff = endDate.getTime() - startDate.getTime();

  // Convert milliseconds to days
  const dayDiff = timeDiff / (1000 * 3600 * 24);

  return Math.floor(dayDiff);
}
