select
  exists
(
  select
    *
  from
    mstknr.create_video_samune_info
  where
    userid = /* userId */''
  and
    video_id = /* videoId */''
)